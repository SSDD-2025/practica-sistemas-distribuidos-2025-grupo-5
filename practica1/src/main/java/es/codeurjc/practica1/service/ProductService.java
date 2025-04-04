package es.codeurjc.practica1.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAllById(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    public boolean exist(long id) {
        return productRepository.findById(id).isPresent();
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product save(Product product, List<Long> selectedUsers) throws IOException {

        if (selectedUsers != null) {
            List<User> products = userService.findByIds(selectedUsers);
            product.setUsers(products);
        }
        return productRepository.save(product);
    }

    public boolean delete(Product product) {
        if (productRepository.existsById(product.getId())) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

}
