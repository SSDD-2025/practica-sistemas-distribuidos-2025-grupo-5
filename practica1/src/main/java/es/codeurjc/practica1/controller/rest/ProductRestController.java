package es.codeurjc.practica1.controller.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
    public Page<ProductDTO> getProducts(Pageable pageable) {
        try {
            return productService.findByDeleteProducts(pageable, false)
                    .map(productMapper::toDTO);
        } catch (Exception e) {
            e.printStackTrace();  // <- Aquí también
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting products");
        }
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable long id) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        try {
            return productMapper.toDTO(product);
        } catch (Exception e) {
            e.printStackTrace();  // <- Este print te dará más información
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error mapping product");
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product existingProduct = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        existingProduct.setName(productDTO.name());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setStock(productDTO.stock());
        existingProduct.setProvider(productDTO.provider());
        existingProduct.setDescription(productDTO.description());
        // existingProduct.setImage(productDTO.image());
        // OJO con reviews: puedes ignorarlas o mapearlas si quieres manejar también eso

        Product updatedProduct = productService.save(existingProduct);
        ProductDTO responseDTO = productMapper.toDTO(updatedProduct);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        Product product = new Product(productDTO.name(), productDTO.description(),productDTO.price(), productDTO.stock(), productDTO.provider(), productDTO.image());
      
        product = productService.save(product);
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
