package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.practica1.model.Product;
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
	private ImageUtils imageUtils;

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

		if(op.isPresent()) {
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

	@PostMapping("/newproduct")
	public String newProductProcess(Model model, Product product, MultipartFile imageField, @RequestParam(required = false) List<Long> selectedUsers) throws IOException {

		Product newProduct = productService.save(product);

		model.addAttribute("productId", newProduct.getId());

		return "redirect:/products/"+newProduct.getId();
	}

	@GetMapping("/cart")
	public String showCart(HttpSession session, Model model) {
		// Obtener el carrito desde la sesión
		List<Long> cartProducts = (List<Long>) session.getAttribute("cart");
	
		if (cartProducts == null || cartProducts.isEmpty()) {
			model.addAttribute("message", "Tu carrito está vacío");
		} else {
			// Obtener los productos por sus IDs
			List<Product> products = new ArrayList<>();
			for (Long productId : cartProducts) {
				Optional<Product> product = productService.findById(productId);
				product.ifPresent(products::add);
			}
	
		}
	
		return "cart";  // Mostrar la vista del carrito
	}
	
	
	

	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session) {
		// Obtener o inicializar el carrito en la sesión
		List<Long> cart = (List<Long>) session.getAttribute("cart");

		if (cart == null) {
			cart = new ArrayList<>();
			System.out.println("CREAMOS NUEVO CARRITO");

		}

		// Agregar el producto al carrito
		cart.add(productId);
		
		session.setAttribute("cart", cart);

		System.out.println("Producto agregado al carrito: " + productId);
		System.out.println(cart);

		return "redirect:/cart";
	}
}