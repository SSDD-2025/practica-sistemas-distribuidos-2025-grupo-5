package es.codeurjc.practica1.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

    public ProductDTO deleteProduct (Long id) {
        ProductDTO productDTO = getProduct(id);
        Product product = toDomain(productDTO);
        if (productRepository.existsById(product.getId())) {
            productRepository.delete(product);
            return toDTO(product);
        }
        return toDTO(product);
    }

    public Resource getProductImage(long id) throws SQLException {

		Product product = productRepository.findById(id).orElseThrow();

		if (product.getImageFile() != null) {
			return new InputStreamResource(product.getImageFile().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}

	public void createProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		product.setImage(true);
		product.setImageFile(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void replaceProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		if (!product.getImage()) {
			throw new NoSuchElementException();
		}

		product.setImageFile(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void deleteProductImage(long id) {

		Product product = productRepository.findById(id).orElseThrow();

		if (!product.getImage()) {
			throw new NoSuchElementException();
		}

		product.setImageFile(null);
		product.setImage(false);

		productRepository.save(product);
	}

    public Product saveProduct(Product product, List<Long> selectedUsers) throws IOException {
        if (selectedUsers != null) {
            List<User> products = userService.findByIds(selectedUsers);
            product.setUsers(products);
        }
        return productRepository.save(product);
    }

    public Product saveP(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(long id) {
       	
        return productRepository.findById(id);
    }

}
