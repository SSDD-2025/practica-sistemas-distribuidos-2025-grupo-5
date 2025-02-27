package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.sql.SQLException;
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

//import es.codeurjc.daw.library.model.Book;
//import es.codeurjc.daw.library.service.BookService;
//import es.codeurjc.daw.library.service.ShopService;

@Controller
public class ProductController {
    
	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String showBooks(Model model) {
		model.addAttribute("books", bookService.findAll());
		return "books";
	}

	@GetMapping("/books/{id}")
	public String showBook(Model model, @PathVariable long id) {

		Optional<Book> book = bookService.findById(id);
		if (book.isPresent()) {
			model.addAttribute("book", book.get());
			return "book";
		} else {
			return "books";
		}

	}

	@GetMapping("/books/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException, IOException {

		Optional<Book> op = bookService.findById(id);

		if(op.isPresent()) {
			Book book = op.get();
			Resource image;
			try {
				image = new InputStreamResource(book.getImageFile().getBinaryStream());
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

		Product newProduct = productService.save(product, selectedUsers, imageField);

		model.addAttribute("productId", newProduct.getId());

		return "redirect:/products/"+newProduct.getId();
	}

}