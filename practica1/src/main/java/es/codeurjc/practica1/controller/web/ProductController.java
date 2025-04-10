package es.codeurjc.practica1.controller.web;

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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
								authentication.isAuthenticated() &&
								!(authentication instanceof AnonymousAuthenticationToken);
		if (isLoggedIn) {
			//tiene que ser asi porque puede ser que te de como válido un usuario anónimo
			boolean isAdmin = authentication.getAuthorities().stream()
											.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
			model.addAttribute("isAdmin", isAdmin);
		}
		System.out.println(isLoggedIn);
		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux=userService.findAllByDeleted(false);
		listAux.remove(0);
		model.addAttribute("users",listAux);		
		model.addAttribute("products", productService.findByDeleteProducts(false));
		return "products";
	}

	@GetMapping("/products/{id}")
	public String showProduct(Model model, @PathVariable long id) {
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			if (isLoggedIn) {
				//tiene que ser asi porque puede ser que te de como válido un usuario anónimo
				boolean isAdmin = authentication.getAuthorities().stream()
												.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
				model.addAttribute("isAdmin", isAdmin);
			}
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
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		return "newProductPage";
	}

	@GetMapping("/showCart")
	public String showCart(HttpSession session, Model model) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");
		List<Product> cartProducts = new ArrayList<>();

		Optional<User> oneUser = userService.findUserByName(authentication.getName());
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

		model.addAttribute("cartProducts", cartProducts);
		model.addAttribute("isEmpty", true);
		
		return "cart"; // Display the cart view.
	}

	@PostMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {
		// TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
			authentication.isAuthenticated() &&
			!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		// ----
	
		// Obtener el usuario
		Optional<User> user = userService.findUserByName(authentication.getName());
	
		if (user.isEmpty()) {
			return "/error"; // o redirigir al login
		}
	
		List<Product> cart = user.get().getProducts();
	
		Optional<Product> productAux = productService.findById(productId);
	
		if (productAux.isEmpty()) {
			return "/error";
		}
	
		Product p = productAux.get();
	
		if (p.getStock() > 0) {
			cart.add(p);
			p.setStock(p.getStock() - 1);
			productService.saveP(p);
			user.get().addProduct(p);
			userService.save(user.get());
		} else {
			return "/error"; // Producto sin stock
		}
	
		model.addAttribute("cartProducts", cart);
		model.addAttribute("isEmpty", cart.isEmpty());
	
		return "redirect:/showCart";
	}
	
	@PostMapping("/remove-from-cart/{productId}")
	public String removeFromCart(@PathVariable long productId, Model model, HttpSession session) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findUserByName(authentication.getName());
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent()) {
			Product p = productAux.get();
			p.setStock(p.getStock() +1);
			productService.saveP(p);
			user.get().removeProduct(p);
			userService.save(user.get());
		}

		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		model.addAttribute("isEmpty", !user.get().getProducts().isEmpty());
		model.addAttribute("cartProducts", user.get().getProducts());
		return "/cart";
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

		Product product = new Product(name, description, price, stock, provider,true);
		
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (!imageField.isEmpty()) {
			Blob imageBlob = imageUtils.createBlob(imageField.getInputStream());
			product.setImageFile(imageBlob);
		}
		if (product.getProvider() == null || product.getName() == null || product.getDescription() == null) {
			return "/error";
		}
		Product newProduct = productService.saveP(product);
		newProduct.setProvider(provider);

		model.addAttribute("productId", newProduct.getId());
		productService.saveP(newProduct);

		return "redirect:/products/" + newProduct.getId();
	}

	@PostMapping("/remove-from-products/{productId}")
	public String removeFromProducts(@PathVariable long productId) {
		// Search for the product in the database.
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent()) {
			productAux.get().setDeletedProducts(true);
			productService.saveP(productAux.get());

		} else {
			return "/error";
		}
		return "redirect:/"; // Redirect to the updated product list.
	}

	@GetMapping("/edit/{id}")
	public String getProductForEdit(@PathVariable Long id, Model model) {
		Optional<Product> product = productService.findById(id);
		if (product.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		model.addAttribute("product", product.get());
		return "editProduct";
	}

	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable Long id,
			@ModelAttribute Product updatedProduct,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

		Optional<Product> existingProduct = productService.findById(id);
		if (existingProduct.isEmpty()) {
			return "/error";
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

		productService.saveP(product);
		return "redirect:/products/" + id; // Correctly redirect to the updated product.
	}
	/*
	 * // Get the list of product IDs in the session.
		Optional<User> oneUser = userService.findUserByName(userDetails.getUsername());
		List<Order> orderList = null;
		
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
	 */

	// ORDER
	@GetMapping("/checkout")
	public String showGateway( HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findUserByName(authentication.getName());
		Order order;
			if (user != null) {
				User userAux=user.get();
				List<Product> cartProduct= userAux.getProducts();
				
				if(cartProduct.size()==1){
					order= new Order(userAux, cartProduct.get(0));
					order.setTotalPrice(cartProduct.get(0).getPrice());
					cartProduct.get(0).setStock(cartProduct.get(0).getStock() - 1);
					productService.saveP(cartProduct.get(0));
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
							productService.saveP(product);
							model.addAttribute("product", product);
	
						} else {
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
						}
					}
				}
				
				orderService.save(order);
				user.get().getProducts().clear();
				userService.addOrder(user.get().getId(), order);
				userService.save(user.get());
				model.addAttribute("orders", order);
				
				//TOOLBAR
				boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
				model.addAttribute("isLoggedIn", isLoggedIn);
				//-----
			}
			return "/gateway";
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@PathVariable Long id, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);
		Order order;
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (productOptional.isPresent()) {
			Product product = productOptional.get();

			if (product.getStock() > 0) {

				product.setStock(product.getStock() - 1);
				Optional<User> optionalUser = userService.findUserByName(authentication.getName());

				order = new Order(optionalUser.get(), product);
				order.setTotalPrice(product.getPrice());
				orderService.save(order);
				
				userService.addOrder(optionalUser.get().getId(), order);
				userService.save(optionalUser.get());

				product.setOrder(order);
				productService.saveP(product);
				model.addAttribute("orders", order);
				
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
			}

		} else {
			return "/error";
		}
		return "/gateway";
	}

	@PostMapping("/removeOrder/{id}")
	public String removeOrder(@PathVariable Long id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> userA = userService.findUserByName(authentication.getName());
		Optional<Order> order = orderService.findById(id);

		try {
			if (order!=null) {

				for (Product product : order.get().getProducts()) {
					product.setStock(product.getStock() + 1);
					productService.saveP(product);
				}
				
				User user = userA.get();
				user.deleteOrder(order.get());  //Elimina de la lista de órdenes
				order.get().deleteAllProducts();  // Limpia productos si es necesario

				userService.save(user);  // Guarda cambios en el usuario
				orderService.delete(order.get());  // Ahora sí borra la orden
		
				return "/";
			} else {
				return "/error";
			}
		
		} catch (Exception e) {
			return "/error";
		}
	}

		// Reviwes
	@GetMapping("/productReviews/{id}")
	public String showReviews(Model model, @PathVariable long id) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		
		if (isLoggedIn) {
			//tiene que ser asi porque puede ser que te de como válido un usuario anónimo
			boolean isAdmin = authentication.getAuthorities().stream()
											.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
			
			
			model.addAttribute("isAdmin", isAdmin);
			
		}
		User user= userService.findUserByName(authentication.getName()).get();
		List<Review> reviews=reviewService.findAllReviews();
			for (Review review: reviews){

				if(review.getAuthor().getName().equals(user.getName())){
					review.setme(true);
					System.err.println("entraaaaaaaaa");
				}else{
					review.setme(false);
				}
			}
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			Product p = product.get();

			model.addAttribute("reviews", p.getReviews());
			return "reviews";
		} else {
			return "/error";
		}
	}

	@GetMapping("/newReview/{productId}")
	public String newReview(@PathVariable long productId, Model model) {
		Optional<Product> productOpt = productService.findById(productId);

		if (!productOpt.isPresent()) {
			return "/error";
		}
		
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		model.addAttribute("product", productOpt.get());
		model.addAttribute("productId", productId);
		return "newReview";
	}

	@PostMapping("/newReview/{productId}")
	public String newReviewProcess(
			@PathVariable long productId,
			@RequestParam String title,
			@RequestParam String text, Model model) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		Optional<Product> productOpt = productService.findById(productId);
		if (!productOpt.isPresent()) {
			return "/error";
		}

		Product product = productOpt.get();
		Optional<User> userOpt = userService.findUserByName(authentication.getName());
		User author = userOpt.get();

		Review review = new Review(title, text, author, product);
		author.addReview(review);
		product.addReview(review);
		userService.save(author);
		reviewService.saveReview(review);
		productService.saveP(product);
		return "redirect:/productReviews/" + product.getId();
	}

	@PostMapping("/removeReview/{reviewId}")
	public String removeReview(@PathVariable long reviewId, HttpSession session, Model model) {
		try {
			List<Review> reviews = (List<Review>) session.getAttribute("reviews");

			Optional<Review> reviewAux = reviewService.findReviewById(reviewId);
			if (!reviewAux.isPresent()) {
				return "/reviews";
			}

			Review review = reviewAux.get();
			User userAux = userService.findUserById(review.getAuthor().getId()).get();//.orElse(null);
			Product productAux = productService.findById(review.getProduct().getId()).orElse(null);

			if (userAux == null || productAux == null) {
				return "/reviews";
			}

			userAux.deleteReview(review);
			productAux.removeReview(review);

			userService.save(userAux);	
			productService.saveP(productAux);

			reviewService.deleteReview(review);

			if (reviews != null) {
				reviews.removeIf(r -> r.getId() == reviewId);
			} else {
				reviews = new ArrayList<>();
			}

			session.setAttribute("reviews", reviews);
			List<Review> updatedReviews = reviewService.findAllReviews();

			session.setAttribute("reviews", updatedReviews);
			return "/productReviews/" + productAux.getId();
		} catch (Exception e) {
			return "/error";
		}
	}

	@PostMapping("/removeReviewByUser/{reviewId}")
	public String removeReviewByUSer(@PathVariable long reviewId, HttpSession session, Model model) {
		try {
			// Obtener el usuario autenticado
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()
					|| authentication instanceof AnonymousAuthenticationToken) {
				return "/access-denied"; // o redireccionar a login
			}

			String username = authentication.getName(); // puede ser el username o el email según tu config

			// Buscar la review
			Optional<Review> reviewAux = reviewService.findReviewById(reviewId);
			if (!reviewAux.isPresent()) {
				return "/reviews";
			}
			boolean isMe=false;
			Review review = reviewAux.get();

			// Verificar que el usuario autenticado es el autor
			if (!review.getAuthor().getName().equals(username)) {
				
				return "/access-denied"; // impedir borrar si no es el autor
			}

			// Continuar con la lógica de borrado
			List<Review> reviews = (List<Review>) session.getAttribute("reviews");

			User userAux = userService.findUserById(review.getAuthor().getId()).get();//.orElse(null);
			Product productAux = productService.findById(review.getProduct().getId()).orElse(null);

			if (userAux == null || productAux == null) {
				return "/reviews";
			}

			userAux.deleteReview(review);
			productAux.removeReview(review);

			userService.save(userAux);
			productService.saveP(productAux);
			reviewService.deleteReview(review);

			if (reviews != null) {
				reviews.removeIf(r -> r.getId() == reviewId);
			} else {
				reviews = new ArrayList<>();
			}

			session.setAttribute("reviews", reviews);
			List<Review> updatedReviews = reviewService.findAllReviews();

			session.setAttribute("reviews", updatedReviews);
			return "/productReviews/" + productAux.getId();

		} catch (Exception e) {
			return "/error";
		}
	}

	@GetMapping("/reviews/{productId}")
	public String showReviews(@PathVariable Long productId, Model model, HttpSession session) {

		try {
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				return "/error";
			}
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean isLoggedIn = authentication != null &&
			authentication.isAuthenticated() &&
			!(authentication instanceof AnonymousAuthenticationToken);

			Product product = productOpt.get();
			List<Review> reviews = product.getReviews();
			User user= userService.findUserByName(authentication.getName()).get();
			for (Review review: reviews){
				if(review.getAuthor().equals(user)){
					review.setme(true);
				}
			}

			session.setAttribute("reviews", reviews);
			model.addAttribute("reviews", reviews);
			
			

			model.addAttribute("isLoggedIn", isLoggedIn);
			//-----
			return "reviews";

		} catch (Exception e) {
			return "/error";
		}
	}




}
	

