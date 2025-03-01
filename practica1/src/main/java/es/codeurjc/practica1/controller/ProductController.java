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

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
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
		System.out.println("USUARIO OPCIONAL"+oneUser);

		if (oneUser.isPresent()) {
			User user =oneUser.get();
			cartProducts=user.getProducts();
			System.out.println("LOS PRODUCTOS SON"+cartProducts);
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
	
	@GetMapping("/add-to-cart/{productId}")
	public String addToCart(@PathVariable long productId, HttpSession session) {
		// Get or initialize the cart in the session.
		List<Long> cart = (List<Long>) session.getAttribute("cart");
		Optional<User> oneUser = userService.findById(0);
		Optional<Product> productAux=productService.findById(productId);

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
			System.out.println("CREAMOS NUEVO CARRITO");
		}

		// Add the product to the cart.
		cart.add(productId);
		session.setAttribute("cart", cart);
		System.out.println("Producto agregado al carrito: " + productId);
		System.out.println(cart);

		return "redirect:/cart";
	}

	@GetMapping("/gateway/{productId}")
	public String showGatewayPage(@PathVariable long productId, Model model) {
    	Optional<Product> product = productService.findById(productId);

		if (product.isPresent()) {
			Product p = product.get();
			if (p.getStock() > 0) {
				p.setStock(p.getStock() - 1);
				productService.save(p);
				model.addAttribute("product", product.get());
				return "gateway";
			}
			else{
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No stock available");
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
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
	@PostMapping("/newproduct")
	public String newProductProcess(
		Model model,
		@RequestParam String name,
		@RequestParam String description,
		@RequestParam double price,
		@RequestParam int stock,
		@RequestParam String provider,
		@RequestParam(required = false) List<Long> selectedUsers,
		@RequestParam("imageField") MultipartFile imageField) throws IOException, SQLException {
	
		Product product = new Product(name, description, price, stock, provider);
	
		if (!imageField.isEmpty()) {
			Blob imageBlob = imageUtils.createBlob(imageField.getInputStream());
			product.setImageFile(imageBlob);
		}

		// Associate users if selected.
		if (selectedUsers != null && !selectedUsers.isEmpty()) {
			List<User> shops = userService.findAllById(selectedUsers);
			product.setUsers(shops);
		}
	
		Product newProduct = productService.save(product);
		model.addAttribute("productId", newProduct.getId());
		return "redirect:/products/" + newProduct.getId();
	}

	@PostMapping("/go-purchase/{productId}")
	public String goPurchaseGateway(@PathVariable long productId, HttpSession session) {
		
		Optional<User> oneUser = userService.findById(0);
		Optional<Product> productAux=productService.findById(productId);

		Product product = null;
		if (productAux.isPresent() && oneUser.isPresent()) {
			product = productAux.get();
			User user =oneUser.get();
			// Save the updated user in the database.
			userService.save(user);
		}

		if (product != null) {
			return "redirect:/gateway/" + product.getId();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
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
	
	@GetMapping("/edit/{id}")
    public String getProductForEdit(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return "/editProduct/{id}";
    }

    /**
     * Save the changes of an edited product.
     */
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, 
	@ModelAttribute Product updatedProduct, 
	@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Optional<Product> existingProduct = productService.findById(id);
		Product product = null;
        if (existingProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        } else{
			product = existingProduct.get();
		}

        // Update product data with the new information from the form.
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());
        product.setProvider(updatedProduct.getProvider());

        // If the user uploads a new image, update it.
        if (imageFile != null && !imageFile.isEmpty()) {
        try {
            Blob newImageBlob = new SerialBlob(imageFile.getBytes()); // Convert MultipartFile to Blob
            product.setImageFile(newImageBlob);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }

        productService.save(product);
        return "redirect:/products/{id}";
    }

}