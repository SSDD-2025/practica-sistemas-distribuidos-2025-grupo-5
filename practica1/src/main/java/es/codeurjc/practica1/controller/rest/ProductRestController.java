package es.codeurjc.practica1.controller.rest;
/* 
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.practica1.dto.ProductDTO;
import es.codeurjc.practica1.service.ProductService;

@RestController
@RequestMapping("/api/Products")
public class ProductRestController {

	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public List<ProductDTO> getProducts() {

		return ProductService.getProducts();
	}

	@GetMapping("/{id}")
	public ProductDTO getProduct(@PathVariable long id) {

		return ProductService.getProduct(id);
	}

	@PostMapping("/")
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO ProductDTO) {

		ProductDTO = ProductService.createProduct(ProductDTO);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(ProductDTO.id()).toUri();

		return ResponseEntity.created(location).body(ProductDTO);
	}

	@PutMapping("/{id}")
	public ProductDTO replaceProduct(@PathVariable long id, @RequestBody ProductDTO updatedProductDTO) throws SQLException {

		return ProductService.replaceProduct(id, updatedProductDTO);
	}

	@DeleteMapping("/{id}")
	public ProductDTO deleteProduct(@PathVariable long id) {

		return ProductService.deleteProduct(id);
	}

	@PostMapping("/{id}/image")
	public ResponseEntity<Object> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		ProductService.createProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		URI location = fromCurrentRequest().build().toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = ProductService.getProductImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

	@PutMapping("/{id}/image")
	public ResponseEntity<Object> replaceProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		ProductService.replaceProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deleteProductImage(@PathVariable long id) throws IOException {

		ProductService.deleteProductImage(id);

		return ResponseEntity.noContent().build();
	}
}*/
