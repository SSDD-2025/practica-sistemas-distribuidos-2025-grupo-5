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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
		model.addAttribute("availableProducts", userService.findAll());
		return "newProductPage";
	}

	@GetMapping("/cart")
	public String showCart(HttpSession session, Model model) {
		
		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");
		List<Product> cartProducts = new ArrayList<>();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Optional<User> oneUser = userService.findByName(authentication.getName());
		System.out.println("USUARIO ESTA EN EL CARRITO");
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

	@GetMapping("/showOrders")
	public String showOrders(HttpSession session, Model model,@AuthenticationPrincipal UserDetails userDetails) {
		System.out.println("USUARIO ESTA EN EL SHOW ORDERS");

		if (userDetails == null) {
			return "redirect:/login"; // o manejar el caso de usuario no autenticado
		}

		// Get the list of product IDs in the session.
		Optional<User> oneUser = userService.findByName(userDetails.getUsername());
		List<Order> orderList = null;
		System.out.println("USUARIO ESTA EN EL SHOW ORDERS");
		
		if (oneUser.isPresent()) {
			User user = oneUser.get();
			orderList = user.getOrders();
		}

		if (orderList.isEmpty()) {
			model.addAttribute("message", "No tienes ningún pedido de momento");
		} else {
			model.addAttribute("orderList", orderList);
		}

		return "orders"; // Display the cart view.
	}

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());

		List<Long> cart = new ArrayList();

		for(Product aux:user.get().getProducts()){
			cart.add(aux.getId());
		}

		cart.add(productId);

		Optional<Product> productAux = productService.findById(productId);
		Product p = productAux.get();
		if (p.getStock() > 0) {
			p.setStock(p.getStock() - 1);
			productService.save(p);
			//model.addAttribute("product", productAux.get());
		} else {
			return "redirect:/error";
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of
			// stock");
		}

		if (productAux.isPresent()) {
			Product product = productAux.get();
			user.get().addProduct(product);
			userService.save(user.get());
		}

		// Add the product to the cart.
		session.setAttribute("cartProducts", cart);
		return "redirect:/cart";
	}

	@PostMapping("/remove-from-cart/{productId}")
	public String removeFromCart(@PathVariable long productId, HttpSession session) {

		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent()) {
			Product p = productAux.get();
			p.setStock(p.getStock() +1);
			productService.save(p);

			user.get().removeProduct(p);
			userService.save(user.get());
		}

		session.setAttribute("cartProducts", user.get().getProducts());
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
		System.out.println("ENTRA EN REMOVE FROM PRODUCTS");

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

	// Reviwes
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
		User author = userOpt.get();

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

	// ORDER
	@GetMapping("/checkout")
	public String showGateway( HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());
		Order order;
			if (user != null) {
				User userAux=user.get();
				List<Product> cartProduct= userAux.getProducts();
				
				if(cartProduct.size()==1){
					order= new Order(userAux, cartProduct.get(0));
					order.setTotalPrice(order.getTotalPrice()+cartProduct.get(0).getPrice());
					cartProduct.get(0).setStock(cartProduct.get(0).getStock() - 1);
					productService.save(cartProduct.get(0));
					cartProduct.get(0).setOrder(order);
					model.addAttribute("product", cartProduct.get(0));

				}else{

					order= new Order(userAux, cartProduct.get(0));
					for (int i = 1; i < cartProduct.size(); i++) {
						Product product =cartProduct.get(i);
	
						if (product.getStock() > 0) {
							order.addProduct(product);
							order.setTotalPrice(order.getTotalPrice()+cartProduct.get(i).getPrice());
							product.setStock(product.getStock() - 1);
							product.setOrder(order);
							productService.save(product);
							model.addAttribute("product", product);
	
						} else {
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
						}
					}
				}
				
				orderService.save(order);
				//System.out.println("Order id (checkoutOne) ="+order.getId());

				userService.addOrder(user.get().getId(), order);
				userService.save(user.get());
				model.addAttribute("orders", order);
				//System.out.println("ORDER ID ANTES GATEWAY EN CHECK OUT"+order.getId());

			}
			return "/gateway";
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);

		//System.out.println(SecurityContextHolder.getContext().getAuthentication());

		if (productOptional.isPresent()) {
			Product product = productOptional.get();

			if (product.getStock() > 0) {

				//System.out.println("PRODUCT EXISTE");
				product.setStock(product.getStock() - 1);
				List<Product> cartProducts = new ArrayList<>();
				cartProducts.add(product);
				Optional<User> optionalUser = userService.findByName(userDetails.getUsername());

				if (optionalUser.isEmpty()) {
					//System.out.println("USER NULO");
					return "redirect:/error";
				} else {

					Order order = new Order(optionalUser.get(), cartProducts);
					order.setTotalPrice(order.getTotalPrice()+product.getPrice());

					orderService.save(order);
					System.out.println("Order id (checkoutOne) ="+order.getId());

					userService.addOrder(optionalUser.get().getId(), order);
					userService.save(optionalUser.get());

					product.setOrder(order);
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

	@PostMapping("/removeOrder/{id}")
	public String removeOrder(@PathVariable Long id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> userA = userService.findByName(authentication.getName());
		Optional<Order> order = orderService.findById(id);


		try {
			if (order!=null) {

				for (Product product : order.get().getProducts()) {
					product.setStock(product.getStock() + 1);
					productService.save(product);
				}
				
				User user = userA.get();
				user.deleteOrder(order.get());  //Elimina de la lista de órdenes
				order.get().deleteAllProducts();  // Limpia productos si es necesario

				userService.save(user);  // Guarda cambios en el usuario
				orderService.delete(order.get());  // Ahora sí borra la orden
		
				return "redirect:/";
			} else {
				System.out.println("Orden no encontrada");
				return "redirect:/error";
			}
		
		} catch (Exception e) {
			System.out.println("SALTA EXCEPCIÓN: " + e.getMessage());
			return "redirect:/error";
		}
		
	}

}
