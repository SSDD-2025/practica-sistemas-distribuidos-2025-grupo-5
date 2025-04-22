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

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.OrderService;
import es.codeurjc.practica1.service.ProductService;
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

	@GetMapping("/")
	public String showProducts(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		if (isLoggedIn) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
			model.addAttribute("isAdmin", isAdmin);
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux = userService.findByDeleted(false);
		listAux.remove(0);
		model.addAttribute("users", listAux);
		model.addAttribute("products", productService.findByDeleteProducts(false).subList(0, 10));
		return "products";
	}

	@GetMapping("/products/{id}")
	public String showProduct(Model model, @PathVariable long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			if (isLoggedIn) {
				boolean isAdmin = authentication.getAuthorities().stream()
						.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
				model.addAttribute("isAdmin", isAdmin);
			}
			model.addAttribute("product", product.get());
			model.addAttribute("isOutOfStock", product.get().getStock() <= 0);

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

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		return "newProductPage";
	}

	@GetMapping("/showCart")
	public String showCart(HttpSession session, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		List<Long> cartProductIds = (List<Long>) session.getAttribute("cart");
		List<Product> cartProducts = new ArrayList<>();

		Optional<User> oneUser = userService.findByName(authentication.getName());
		if (oneUser.isPresent()) {
			User user = oneUser.get();
			cartProducts = user.getProducts();

			if (cartProductIds != null && !cartProductIds.isEmpty()) {
				// Search for each product by ID and add it to the list.
				for (Long productId : cartProductIds) {
					productService.findById(productId).ifPresent(cartProducts::add);
				}
			}
			model.addAttribute("isEmpty", !user.getProducts().isEmpty());
			model.addAttribute("cartProducts", cartProducts);

			return "cart"; // Display the cart view.
		} else {
			return "/login";
		}

	}

	@PostMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		Optional<User> user = userService.findByName(authentication.getName());

		if (user.isEmpty()) {
			model.addAttribute("message", "El usuario no existe.");
			return "/error";
		}

		List<Product> cart = user.get().getProducts();

		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isEmpty()) {
			model.addAttribute("message", "El producto no existe.");
			return "/error";
		}

		Product p = productAux.get();

		if (p.getStock() > 0) {
			cart.add(p);
			model.addAttribute("isOutOfStock", true);
			p.setStock(p.getStock());
			productService.save(p);
			user.get().addProduct(p);
			userService.save(user.get());
		} else {
			model.addAttribute("isOutOfStock", false);
			model.addAttribute("message", "El producto no está disponible en stock.");
			return "/error";
		}

		model.addAttribute("cartProducts", cart);
		model.addAttribute("isEmpty", cart.isEmpty());
		return "redirect:/showCart";
	}

	@PostMapping("/remove-from-cart/{productId}")
	public String removeFromCart(@PathVariable long productId, Model model, HttpSession session) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());
		Optional<Product> productAux = productService.findById(productId);

		if (productAux.isPresent()) {
			Product p = productAux.get();
			p.setStock(p.getStock() + 1);
			productService.save(p);
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

		Blob imageBlob = null;
		if (!imageField.isEmpty()) {
			imageBlob = imageUtils.createBlob(imageField.getInputStream());
		}
		Product product = new Product(name, description, price, stock, provider, imageBlob);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		if (product.getProvider() == null || product.getName() == null || product.getDescription() == null) {
			model.addAttribute("message", "El producto no se ha podido crear, faltan datos.");
			return "/error";
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
			productAux.get().setDeletedProducts(true);
			productAux.get().setStock(0);
			productService.save(productAux.get());

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

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		model.addAttribute("product", product.get());

		return "editProduct";
	}

	@PostMapping("/update/{id}")
	public String updateProduct(@PathVariable Long id,
			@ModelAttribute Product updatedProduct,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile, Model model) {

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
		model.addAttribute("isOutOfStock", product.getStock() <= 0);

		productService.save(product);
		return "redirect:/products/" + id; // Correctly redirect to the updated product.
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@PathVariable Long id, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);
		Order order;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();

			if (product.getStock() > 0) {

				product.setStock(product.getStock() - 1);
				Optional<User> optionalUser = userService.findByName(authentication.getName());

				order = new Order(optionalUser.get(), product);
				order.setTotalPrice(product.getPrice());
				orderService.save(order);

				userService.addOrder(optionalUser.get().getId(), order);
				userService.save(optionalUser.get());

				product.setOrder(order);
				productService.save(product);
				model.addAttribute("orders", order);

			} else {
				model.addAttribute("isOutOfStock", product.getStock() <= 0);
				model.addAttribute("message", "Producto sin stock");

				return "/error";
			}

		} else {
			model.addAttribute("message", "No product found with id: " + id);
			return "/error";
		}
		return "/gateway";
	}

	// header.html
	@GetMapping("/search")
	public String searchProducts(@RequestParam("query") String query, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		if (isLoggedIn) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
			model.addAttribute("isAdmin", isAdmin);
		}

		List<Product> results = productService.searchProducts(query);

		if (results == null || results.isEmpty()) {
			model.addAttribute("message", "No se encontraron productos que coincidan con la búsqueda.");
			return "error";
		}

		if (results.size() == 1) {
			return "redirect:/products/" + results.get(0).getId();
		}

		model.addAttribute("products", results);
		return "products";
	}

}
