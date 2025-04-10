package es.codeurjc.practica1.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.dto.ProductDTO;
import es.codeurjc.practica1.dto.ProductMapper;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private UserService userService;

    private Product toDomain(ProductDTO productDTO) {
		return mapper.toDomain(productDTO);
	}

	private List<ProductDTO> toDTOs(List<Product> Products) {
		return mapper.toDTOs(Products);
	}

    private ProductDTO toDTO(Product product) {
		return mapper.toDTO(product);
	}

    public ProductDTO getProduct(long id) {
       	
        return toDTO(productRepository.findById(id).orElseThrow());
    }

    public List<ProductDTO> findAllById(List<Long> productIds) {
        return toDTOs (productRepository.findAllById(productIds));
    }

    public List<ProductDTO> findByDeleteProducts(Boolean deletedProducts) {
        return toDTOs (productRepository.findByDeletedProducts(deletedProducts));
    }

    public boolean exist(long id) {
        return productRepository.findById(id).isPresent();
    }

    public List<ProductDTO> findProducts() {

        return toDTOs (productRepository.findAll());
    }

    public ProductDTO save(ProductDTO productDTO) {

        Product product =  toDomain(productDTO);
        productRepository.save(product);
        return toDTO(product);
    }

    public ProductDTO save(ProductDTO productDTO, List<Long> selectedUsers) throws IOException {

        Product product = toDomain(productDTO);
        if (selectedUsers != null) {
            List<User> products = userService.findByIds(selectedUsers);
            product.setUsers(products);
        }
        productRepository.save(product);

        return toDTO(product);
    }

    public ProductDTO deleteProduct (ProductDTO productDTO) {

        Product product = toDomain(productDTO);
        if (productRepository.existsById(product.getId())) {
            productRepository.delete(product);
            return toDTO(product);
        }
        return toDTO(product);
    }

}
