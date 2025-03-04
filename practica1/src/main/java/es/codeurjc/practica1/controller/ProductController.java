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

	@GetMapping("/productReviews/{id}")
	public String showReviews(Model model, @PathVariable long id) {
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			Product p = product.get();
			//model.addAttribute("product", p.getId());

			model.addAttribute("reviews", p.getReviews());
			return "reviews";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/products/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException, IOException {

		Optional<Product> op = productService.findById(id);

		if(op.isPresent()){
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
		}else {
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
			User user =oneUser.get();
			cartProducts=user.getProducts();
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
	
		return "cart";  // Display the cart view.
	}
	
	@GetMapping("/checkout")
	public String showGateway(HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");

		List<User> oneUser = userService.findAll();
		User user= oneUser.get(0);
		if(cartProductIds.isEmpty()){
			return "redirect:/";
		}else{
			if (user!=null) {
				List<Product> cartProducts = new ArrayList<>();

				for(int i=0; i<cartProductIds.size(); i++){
					Long productId = cartProductIds.get(i);
            		Optional<Product> aux = productService.findById(productId);
					Product product = aux.get();
					if (product.getStock() > 0) {
						product.setStock(product.getStock() - 1);
						productService.save(product);
						model.addAttribute("product", aux.get());
					}
					else{
						throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
					}
					cartProducts.add(product);
				}

				Order order = new Order(user, cartProducts);
				orderService.save(order);
				user.setOrder(order);
				userService.save(user);
				model.addAttribute("orders", order);
				System.out.println("ORDER"+ order);
			}
			return "gateway";
		}
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@PathVariable Long id,HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);	

		if (productOptional.isPresent()) {
			Product product = productOptional.get();

			if (product.getStock() > 0) {
				//actualizamos el stock del producto
				product.setStock(product.getStock() - 1);
				
				//creamos una lista de productos
				List<Product> cartProducts = new ArrayList<>();
				//añadimos el producto a la lista
				cartProducts.add(product);	
//-------------
				//buscamos el usuario 0
				List <User> oneUser = userService.findAll();
				//Optional<User> oneUser = userService.findById(0);
				User user= oneUser.get(0);
				if(user==null){

					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
				}else{
			
					//creamos un pedido con el usuario y el producto
					Order order = new Order(user, cartProducts);
					//guardamos el pedido
					orderService.save(order);
					//guardamos el pedido en el usuario
					user.setOrder(order);
					userService.save(user);
					
					//añadimos el pedido a la lista de pedidos que tiene un producto
					product.getOrders(order);
					productService.save(product);	
					model.addAttribute("orders", order);

//---------------	
				}

	
			}else{
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
			}
			
		} else {
			return "redirect:/";
		}
		return "gateway";
	}



	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {
		// Get or initialize the cart in the session.
		List<Long> cart = (List<Long>) session.getAttribute("cart");
		Optional<User> oneUser = userService.findById(0);
		Optional<Product> productAux=productService.findById(productId);

		Product p = productAux.get();
		if (p.getStock() > 0) {
			p.setStock(p.getStock() - 1);
			productService.save(p);
			model.addAttribute("product", productAux.get());
		}
		else{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
		}

		if (productAux.isPresent() && oneUser.isPresent()) {
			Product product = productAux.get();
			User user =oneUser.get();
			user.addProduct(product);
			// Save the updated user in the database.
			userService.save(user);
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
		Optional<Product> productAux=productService.findById(productId);

		if (productAux.isPresent() && oneUser.isPresent()) {
			Product product = productAux.get();
			User user =oneUser.get();
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

	@GetMapping("/reviews/{productId}")
	public String showReviews(@PathVariable Long productId, Model model, HttpSession session) {
		try {
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				System.out.println("Producto no encontrado con ID: " + productId);
				return "redirect:/error"; // Redirigir a una página de error si el producto no existe
			}
	
			Product product = productOpt.get();
			List<Review> reviews = product.getReviews(); // Obtener las reseñas del producto
	
			session.setAttribute("reviews", reviews); // Guardar en la sesión
			model.addAttribute("reviews", reviews);   // Pasar al modelo
	
			return "reviews"; // Renderizar reviews.html
	
		} catch (Exception e) {
			System.out.println("Error al obtener las reseñas: " + e.getMessage());
			return "redirect:/error"; // Página de error
		}
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
	
			// Actualizar la lista de reseñas en la sesión
			if (reviews != null) {
				reviews.removeIf(r -> r.getId() == reviewId);
			} else {
				reviews = new ArrayList<>();
			}

			session.setAttribute("reviews", reviews);
			List<Review> updatedReviews = reviewService.findAll();

			session.setAttribute("reviews", updatedReviews);
			return "redirect:/reviews/"  + productAux.getId();
	
		} catch (Exception e) {
			return "redirect:/error"; 
		}
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
		Product newProduct = productService.save(product);
		newProduct.setProvider(provider);

		model.addAttribute("productId", newProduct.getId());
		productService.save(newProduct);

		return "redirect:/products/" + newProduct.getId();
	}


	@PostMapping("/newReview/{productId}")
	public String newReviewprocess(
		@PathVariable long productId,
		Model model,
		@RequestParam String title,
		@RequestParam String text,
		@RequestParam List<String> description) throws IOException, SQLException {

		Optional<Product> productAux = productService.findById(productId);
		Product product = productAux.get();
		Optional<User> user= userService.findById(0);
		User author = user.get();
		
		Review review = new Review(title, text, author, product);
		if (description != null) {
			review.setComments(description);
		}
		product.addReview(review);
		productService.save(product);
		reviewService.save(review);

		return "redirect:/reviews";
	}



	@PostMapping("/remove-from-products/{productId}")
	public String removeFromProducts(@PathVariable long productId) {
		// Search for the product in the database.
		Optional<Product> productAux = productService.findById(productId);
	
		if (productAux.isPresent()) {
			productService.delete(productAux.get()); // Delete the product from the database.
			System.out.println("Producto eliminado: " + productId);
		} else {
			System.out.println("Error: Producto no encontrado");
		}
		return "redirect:/"; // Redirect to the updated product list.
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
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
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
	


}