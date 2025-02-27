package es.codeurjc.practica1.service;
    
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    public Optional<Product> findById(long id){
        return productRepository.findById(id);
    }

    public boolean exist(long id) {
        return productRepository.findById(id).isPresent();
    }

    public List<Product> findAll(){
        return productRepository.findAll();
    }
    
    public Product save(Product product){
        return productRepository.save(product);
    }
    public Product save(Product product, MultipartFile imageField) throws IOException {

		if (imageField != null && !imageField.isEmpty()){
			product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
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

