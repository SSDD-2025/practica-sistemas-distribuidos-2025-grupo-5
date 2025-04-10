package es.codeurjc.practica1.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
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
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.ProductDTO;
import es.codeurjc.practica1.dto.UserDTO;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.UserService;

@RestController
@RequestMapping("/api/books")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<ProductDTO> getProducts() {
        return productService.findProducts();
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable long id) {

        return productService.getProduct(id);
    }

    @PostMapping("/")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO ProductDTO) {

        ProductDTO = productService.save(ProductDTO);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(ProductDTO.id()).toUri();

        return ResponseEntity.created(location).body(ProductDTO);
    }

    @DeleteMapping("/{id}")
    public ProductDTO deleteProduct(@PathVariable long id) {

        return productService.deleteProduct(id);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        productService.createProductImage(id, imageFile.getInputStream(), imageFile.getSize());

        URI location = fromCurrentRequest().build().toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

        Resource postImage = productService.getProductImage(id);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(postImage);

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> replaceProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        productService.replaceProductImage(id, imageFile.getInputStream(), imageFile.getSize());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteProductImage(@PathVariable long id) throws IOException {

        productService.deleteProductImage(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public String showProducts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        if (isLoggedIn) {
            // tiene que ser asi porque puede ser que te de como válido un usuario anónimo
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        System.out.println(isLoggedIn);
        model.addAttribute("isLoggedIn", isLoggedIn);
        List<UserDTO> listAux = userService.findByDeleted(false);
        listAux.remove(0);
        model.addAttribute("users", listAux);
        model.addAttribute("products", productService.findByDeleteProducts(false));
        return "products";
    }

    @GetMapping("/products/{id}")
    public String showProduct(Model model, @PathVariable long id) {
        // TOOLBAR
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isLoggedIn", isLoggedIn);
        // -----

        // Obtener el ProductDTO en lugar de la entidad Product
        ProductDTO productDTO = productService.getProduct(id); // Cambiamos esto para obtener el DTO

        if (productDTO != null) { // Ahora productDTO es un ProductDTO, no un Optional<Product>
            if (isLoggedIn) {
                // Verifica si el usuario es administrador
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                model.addAttribute("isAdmin", isAdmin);
            }
            model.addAttribute("product", productDTO); // Pasamos el ProductDTO al modelo
            return "product"; // Devuelve la vista de detalle del producto
        } else {
            // Si no se encuentra el producto, redirige a la lista de productos
            return "products"; // Devuelve la vista con la lista de productos
        }
    }
}



