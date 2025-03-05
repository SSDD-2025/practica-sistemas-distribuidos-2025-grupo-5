package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.OrderService;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.ReviewService;
import es.codeurjc.practica1.service.UserService;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ImageUtils imageUtils;
	@Autowired
	private ReviewService reviewService;

	@GetMapping("/")
	public String showProducts(Model model) {

		model.addAttribute("products", productService.findAll());
		return "products";
	}

	@GetMapping("/products/{id}")
	public String showProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			model.addAttribute("product", product.get());
			return "product";
		} else {
			return "products";
		}
	}

	@GetMapping("/products/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException, IOException {

		Optional<Product> op = productService.findById(id);

		if (op.isPresent()) {
			Product product = op.get();
			Resource image;
			try {
				image = new InputStreamResource(product.getImageFile().getBinaryStream());
			} catch (Exception e) {
				ClassPathResource resource = new ClassPathResource("static/no-image.png");
				byte[] imageBytes = resource.getInputStream().readAllBytes();
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(imageBytes);
			}
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
		}
	}

	@GetMapping("/newproduct")
	public String newProduct(Model model) {
		model.addAttribute("availableShops", userService.findAll());
		return "newProductPage";
	}

	@GetMapping("/cart")
	public String showCart(HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");
		List<Product> cartProducts = new ArrayList<>();

		Optional<User> oneUser = userService.findById(0);

		if (oneUser.isPresent()) {
			User user = oneUser.get();
			cartProducts = user.getProducts();
		}

		if (cartProductIds != null && !cartProductIds.isEmpty()) {
			// Search for each product by ID and add it to the list.
			for (Long productId : cartProductIds) {
				productService.findById(productId).ifPresent(cartProducts::add);
			}
		}

		if (cartProducts.isEmpty()) {
			model.addAttribute("message", "Tu carrito está vacío");
		} else {
			model.addAttribute("cartProducts", cartProducts);
		}

		return "cart"; // Display the cart view.
	}

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {
		// Get or initialize the cart in the session.
		List<Long> cart = (List<Long>) session.getAttribute("cart");
		Optional<User> oneUser = userService.findById(0);
		Optional<Product> productAux = productService.findById(productId);

		Product p = productAux.get();
		if (p.getStock() > 0) {
			p.setStock(p.getStock() - 1);
			productService.save(p);
			model.addAttribute("product", productAux.get());
		} else {
			return "redirect:/error";
			//throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
			
		}

		if (productAux.isPresent() && oneUser.isPresent()) {
			Product product = productAux.get();
			User user = oneUser.get();
			user.addProduct(product);
			// Save the updated user in the database.
			userService.save(user);
			System.out.println(user.getProducts());
		}

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
		}

		// Add the product to the cart.
		cart.add(productId);
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}

	@PostMapping("/remove-from-cart/{productId}")
	public String removeFromCart(@PathVariable long productId, HttpSession session) {
		// Get or initialize the cart in the session.
		List<Long> cart = (List<Long>) session.getAttribute("cart");
		Optional<User> oneUser = userService.findById(0);
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent() && oneUser.isPresent()) {
			Product product = productAux.get();
			User user = oneUser.get();
			user.removeProduct(product);
			// Save the updated user in the database.
			userService.save(user);
		}

		if (cart == null) {
			cart = new ArrayList<>();
			session.setAttribute("cart", cart);
			System.out.println("CREAMOS NUEVO CARRITO");
		}
		// Remove the product from the cart.
		cart.remove(productId);
		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}


	@PostMapping("/newproduct")
	public String newProductProcess(
			Model model,
			@RequestParam String name,
			@RequestParam String description,
			@RequestParam double price,
			@RequestParam int stock,
			@RequestParam String provider,
			@RequestParam("imageField") MultipartFile imageField) throws IOException, SQLException {

		Product product = new Product(name, description, price, stock, provider);

		if (!imageField.isEmpty()) {
			Blob imageBlob = imageUtils.createBlob(imageField.getInputStream());
			product.setImageFile(imageBlob);
		}
		if (product.getProvider() == null || product.getName() == null || product.getDescription() == null) {
			return "redirect:/error";
		}
		Product newProduct = productService.save(product);
		newProduct.setProvider(provider);

		model.addAttribute("productId", newProduct.getId());
		productService.save(newProduct);

		return "redirect:/products/" + newProduct.getId();
	}

	@PostMapping("/remove-from-products/{productId}")
	public String removeFromProducts(@PathVariable long productId) {
		// Search for the product in the database.
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent()) {
			productService.delete(productAux.get()); // Delete the product from the database.
			System.out.println("Producto eliminado: " + productId);
		} else {
			return "redirect:/error";
		}
		return "redirect:/"; // Redirect to the updated product list.
	}


	@GetMapping("/edit/{id}")
	public String getProductForEdit(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.findById(id);
		if (product.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
		model.addAttribute("product", product.get());
		return "editProduct";
	}
	
	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable Long id,
			@ModelAttribute Product updatedProduct,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

		Optional<Product> existingProduct = productService.findById(id);
		if (existingProduct.isEmpty()) {
			return "redirect:/error";
		}

		Product product = existingProduct.get();
		product.setName(updatedProduct.getName());
		product.setDescription(updatedProduct.getDescription());
		product.setPrice(updatedProduct.getPrice());
		product.setStock(updatedProduct.getStock());
		product.setProvider(updatedProduct.getProvider());

		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				Blob newImageBlob = new SerialBlob(imageFile.getBytes());
				product.setImageFile(newImageBlob);
			} catch (SQLException | IOException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing image");
			}
		}

		productService.save(product);
		return "redirect:/products/" + id; // Correctly redirect to the updated product.
	}


	//Reviwes

    @GetMapping("/productReviews/{id}")
	public String showReviews(Model model, @PathVariable long id) {
        System.err.println("ENTRA EN SHOW REVIEWS");
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			Product p = product.get();

			model.addAttribute("reviews", p.getReviews());
			return "reviews";
		} else {
			return "redirect:/error";
		}
	}


	@GetMapping("/newReview/{productId}")
	public String newReview(@PathVariable long productId, Model model) {
		Optional<Product> productOpt = productService.findById(productId);

		if (!productOpt.isPresent()) {
			return "redirect:/error";
		}

		model.addAttribute("product", productOpt.get());
		model.addAttribute("productId", productId);
		return "newReview";
	}

	@PostMapping("/newReview/{productId}")
	public String newReviewProcess(
			@PathVariable long productId,
			@RequestParam String title,
			@RequestParam String text) {

		System.out.println("ENTRA EN NEW REVIEW");

		Optional<Product> productOpt = productService.findById(productId);
		if (!productOpt.isPresent()) {
			return "redirect:/error";
		}
		Product product = productOpt.get();

		Optional<User> userOpt = userService.findByEmail("paula@gmail.com");
		User author = userOpt.orElseGet(() -> {
			User newUser = new User("paula", "paula@gmail.com", "1234", 0, 432436273);
			return userService.save(newUser); // Guarda el usuario si no existe
		});

		Review review = new Review(title, text, author, product);
		product.addReview(review);

		reviewService.save(review);
		productService.save(product);
		return "redirect:/productReviews/" + product.getId();
	}

    
    @PostMapping("/removeReview/{reviewId}")
	public String removeReview(@PathVariable long reviewId, HttpSession session) {
		try {
			List<Review> reviews = (List<Review>) session.getAttribute("reviews");

			Optional<Review> reviewAux = reviewService.findById(reviewId);
			if (!reviewAux.isPresent()) {
				return "redirect:/reviews";
			}

			Review review = reviewAux.get();
			User userAux = userService.findById(review.getAuthor().getId()).orElse(null);
			Product productAux = productService.findById(review.getProduct().getId()).orElse(null);

			if (userAux == null || productAux == null) {
				return "redirect:/reviews";
			}

			userAux.deleteReview(review);
			productAux.removeReview(review);

			userService.save(userAux);
			productService.save(productAux);

			reviewService.delete(review);

			if (reviews != null) {
				reviews.removeIf(r -> r.getId() == reviewId);
			} else {
				reviews = new ArrayList<>();
			}

			session.setAttribute("reviews", reviews);
			List<Review> updatedReviews = reviewService.findAll();

			session.setAttribute("reviews", updatedReviews);
			return "redirect:/productReviews/" + productAux.getId();
		} catch (Exception e) {
			return "redirect:/error";
		}
	}

    @GetMapping("/reviews/{productId}")
	public String showReviews(@PathVariable Long productId, Model model, HttpSession session) {
		try {
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				return "redirect:/error";
			}

			Product product = productOpt.get();
			List<Review> reviews = product.getReviews();

			session.setAttribute("reviews", reviews); 
			model.addAttribute("reviews", reviews);
			System.out.println("Reseñas del producto ");
			return "reviews"; 

		} catch (Exception e) {
			return "redirect:/error";
		}
	}

	//ORDER
	@GetMapping("/checkout")
	public String showGateway(HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");

		List<User> oneUser = userService.findAll();
		User user = oneUser.get(0);
		if (cartProductIds.isEmpty()) {
			return "redirect:/error";
		} else {
			if (user != null) {
				List<Product> cartProducts = new ArrayList<>();

				for (int i = 0; i < cartProductIds.size(); i++) {
					Long productId = cartProductIds.get(i);
					Optional<Product> aux = productService.findById(productId);
					Product product = aux.get();
					if (product.getStock() > 0) {
						product.setStock(product.getStock() - 1);
						productService.save(product);
						model.addAttribute("product", aux.get());
					} else {
						throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
					}
					cartProducts.add(product);
				}
				Order order = new Order(user, cartProducts);
				orderService.save(order);
				user.setOrder(order);
				userService.save(user);
				model.addAttribute("orders", order);
			}
			return "/gateway";
		}
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@PathVariable Long id, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			if (product.getStock() > 0) {
				product.setStock(product.getStock() - 1);

				List<Product> cartProducts = new ArrayList<>();
				cartProducts.add(product);

				List<User> oneUser = userService.findAll();
				User user = oneUser.get(0);
				if (user == null) {

					return "redirect:/error";
				} else {

					Order order = new Order(user, cartProducts);
					orderService.save(order);
					user.setOrder(order);
					userService.save(user);

					product.getOrders(order);
					productService.save(product);
					model.addAttribute("orders", order);

				}

			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
			}

		} else {
			return "redirect:/error";
		}
		return "/gateway";
	}


	@PostMapping("/removeOrder/{orderId}")
	public String removeOrder(@PathVariable long orderId, HttpSession session) {
		try {
			Optional<Order> orderAux = orderService.findById(orderId);
			Order order = orderAux.get();
			System.out.println("ANTES ORDER: " + order);
			User user = order.getOwner();

			user.deleteOrder(order);
			order.deleteAllProducts();

			userService.save(user);
			orderService.delete(order);
			System.out.println("DESPUES ");
			return "redirect:/";
			
		} catch (Exception e) {
			return "redirect:/error";
		}
	}
}
