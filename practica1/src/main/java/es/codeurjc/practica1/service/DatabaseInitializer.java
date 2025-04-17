package es.codeurjc.practica1.service;

import java.io.IOException;
import java.util.List;

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
        List<String> rolAdmin = List.of("ADMIN");
        userService.save(new User("admin", "paula@gmail.com", passwordEncoder.encode("adminpass"), rolAdmin, 912));
        userService.save(new User("user", "juanjo@gmail.com", passwordEncoder.encode("pass"), rolUser, 112));
        User user1 = userService.findByName("user").get();
        System.out.println("ID USUARIO: " + user1.getId());

        List<Long> set = List.of(user1.getId());
        // User user2 = new User( "juanjo", "juanjo@gmail.com", "567",0, 987654321 );
        // UserService.save(user2);
        // set = List.of(user2.getId());

        Product product1 = new Product("Cuerda", "resistente", 12.3, 123, "ES_factory", "rope.jpg");
        productService.save(product1);
        Review review2 = new Review("Cuerda", "no aguanta", user1, product1);
        reviewService.save(review2);

        Product product2 = new Product("Gafas", "para el sol", 56.3, 123, "GLLASSES_factory", "glasses.jpg");
        productService.save(product2);
        Review review1 = new Review("Gafas", "no son de sol", user1, product2);
        Review review3 = new Review("ggg", "ggg", user1, product2);
        reviewService.save(review3);
        productService.save(product2);

        reviewService.save(review1);

    }



}