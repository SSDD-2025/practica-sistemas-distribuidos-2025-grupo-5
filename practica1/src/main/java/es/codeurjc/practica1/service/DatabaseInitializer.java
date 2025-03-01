package es.codeurjc.practica1.service;
import java.io.IOException;
import java.util.List;

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

    private Product saveProductWithURLImage(Product product, List<Long> selectedUsers,String image) throws IOException {
		product.setImageFile(imageUtils.localImageToBlob("/Users/paulamarcelabarrosorobleda/Desktop/practica-sistemas-distribuidos-2025-grupo-5/images/" + image));
		return productService.save(product,selectedUsers);
	}
}
