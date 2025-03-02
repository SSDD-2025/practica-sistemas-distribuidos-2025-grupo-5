package es.codeurjc.practica1.service;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.annotation.PostConstruct;
@Component
public class DatabaseInitializer {

    @Autowired
    private UserService UserService;

    @Autowired
    private ProductService productService;

    @Autowired
	private ImageUtils imageUtils;

    @PostConstruct
    public void init() throws IOException {
        
        User user1 = new User( "paula", "paula@gmail.com", "567",0, 123456789 );
        System.out.println("ID USUARIO: "+user1.getId());
        UserService.save(user1);
        List<Long> set = List.of(user1.getId());

        // Create some books
        Product product1 = new Product("Cuerda","resistente", 12.3, 123,"ES_factory");
        saveProductWithURLImage(product1,set,"rope.jpg");

        Product product2 = new Product("Gafas","para el sol", 56.3, 123,"GLLASSES_factory");
        saveProductWithURLImage(product2,set,"glasses.jpg");
    }

    private Product saveProductWithURLImage(Product product, List<Long> selectedUsers, String image) {
        try {
            // GitHub RAW URL to access the image.
            String imageUrl = "https://raw.githubusercontent.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/main/images/" + image;

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
            return null; //Or handle with a custom exception.
        }
    }

}
