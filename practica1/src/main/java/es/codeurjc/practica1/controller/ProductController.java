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

import es.codeurjc.practica1.model.Product;
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
			
			if (isAdmin) {
				model.addAttribute("isAdmin", isAdmin);
				System.out.println("El usuario es ADMIN");
			} else {
				model.addAttribute("isAdmin", isAdmin);
				System.out.println("El usuario NOOOOO es ADMIN");
			}
		}

		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux=userService.findAll();
		listAux.remove(0);
		model.addAttribute("users",listAux);		model.addAttribute("products", productService.findAll());
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
				
				if (isAdmin) {
					model.addAttribute("isAdmin", isAdmin);
					System.out.println("El usuario es ADMIN");
				} else {
					model.addAttribute("isAdmin", isAdmin);
					System.out.println("El usuario NOOOOO es ADMIN");
	
				}
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
		model.addAttribute("availableProducts", userService.findAll());
		return "newProductPage";
	}

	@GetMapping("/cart")
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
			model.addAttribute("isEmpty", false);
		} else {
			model.addAttribute("cartProducts", cartProducts);
			model.addAttribute("isEmpty", true);

		}
		return "cart"; // Display the cart view.
	}

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//----

		// Get the list of product IDs in the session.
		Optional<User> user = userService.findByName(authentication.getName());

		List<Long> cart = new ArrayList();

		for(Product aux:user.get().getProducts()){
			cart.add(aux.getId());
			System.out.println("id productos carro"+aux.getId());
		}

		cart.add(productId);

		Optional<Product> productAux = productService.findById(productId);
		Product p = productAux.get();
		if (p.getStock() > 0) {
			p.setStock(p.getStock() - 1);
			productService.save(p);
			//model.addAttribute("product", productAux.get());
		} else {
			return "/error";
			// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of
			// stock");
		}

		Product product = productAux.get();
		user.get().addProduct(product);
		userService.save(user.get());
		

		// Add the product to the cart.
		session.setAttribute("cartProducts", cart);
		return "/cart";
	}

	@PostMapping("/remove-from-cart/{productId}")
	public String removeFromCart(@PathVariable long productId, HttpSession session) {
		
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

		Product product = new Product(name, description, price, stock, provider);
		
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
		Product newProduct = productService.save(product);
		newProduct.setProvider(provider);

		model.addAttribute("productId", newProduct.getId());
		productService.save(newProduct);

		return "/products/" + newProduct.getId();
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
			return "/error";
		}
		return "/"; // Redirect to the updated product list.
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

		productService.save(product);
		return "/products/" + id; // Correctly redirect to the updated product.
	}

}
