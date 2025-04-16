package es.codeurjc.practica1.controller.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.ProductDTO;
import es.codeurjc.practica1.dto.ProductMapper;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/")
    public List<ProductDTO> getProducts() {
        List<ProductDTO> products = new ArrayList<>();
        for (Product product : productService.findAll()) {
            products.add(productMapper.toDTO(product));
        }
            
        return products;
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable long id) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }

    @PostMapping("/")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = productMapper.toDomain(productDTO);
        product = productService.save(product); // guarda y actualiza con ID
        ProductDTO responseDTO = productMapper.toDTO(product);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ProductDTO deleteProduct(@PathVariable long id) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        productService.delete(product);
        return productMapper.toDTO(product);
    }

}


