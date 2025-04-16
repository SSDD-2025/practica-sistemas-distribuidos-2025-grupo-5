package es.codeurjc.practica1.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageUtils imageUtils;

    @PostConstruct
    public void init() throws IOException {
        List<String> rolUser = List.of("USER");
        List<String> rolAdmin = List.of( "ADMIN");
        userService.save(new User("admin","paula@gmail.com", passwordEncoder.encode("adminpass"), rolAdmin,912));
        userService.save(new User("user","juanjo@gmail.com",passwordEncoder.encode("pass"), rolUser,112));
        User user1 = userService.findByName("user").get();
        System.out.println("ID USUARIO: " + user1.getId());
        
        List<Long> set = List.of(user1.getId());
        // User user2 = new User( "juanjo", "juanjo@gmail.com", "567",0, 987654321 );
        // UserService.save(user2);
        // set = List.of(user2.getId());

        Product product1 = new Product("Cuerda", "resistente", 12.3, 123, "ES_factory");
        productService.save(product1);
        saveProductWithURLImage(product1, set, "rope.jpg");
        Review review2 = new Review("Cuerda", "no aguanta", user1, product1);
        reviewService.save(review2);
   

        Product product2 = new Product("Gafas", "para el sol", 56.3, 123, "GLLASSES_factory");
        productService.save(product2);
        saveProductWithURLImage(product2, set, "glasses.jpg");
        Review review1 = new Review("Gafas", "no son de sol", user1, product2);
        Review review3 = new Review("ggg", "ggg", user1, product2);
        reviewService.save(review3);
        productService.save(product2);

        reviewService.save(review1);

    }

    private Product saveProductWithURLImage(Product product, List<Long> selectedUsers, String image) {
        try {
            // GitHub RAW URL to access the image.
            String imageUrl = "https://raw.githubusercontent.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/tree/main/images/"
                    + image;

            // Download the image from the URL.
            InputStream imageStream = new URL(imageUrl).openStream();
            byte[] imageBytes = imageStream.readAllBytes(); // Java 9+

            // Convert the byte[] into a Blob.
            Blob imageBlob = new SerialBlob(imageBytes);
            product.setImageFile(imageBlob);

            // Save the product with the image.
            return productService.save(product, selectedUsers);
        } catch (Exception e) {
            e.printStackTrace(); // Print error in console for debugging.
            return null; // Or handle with a custom exception.
        }
    }

}