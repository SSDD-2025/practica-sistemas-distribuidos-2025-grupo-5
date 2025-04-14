package es.codeurjc.practica1.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
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
    private UserService userService;

    @Autowired
    private ProductMapper mapper;

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAllById(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    public List<Product> findByDeleteProducts(Boolean deletedProducts) {
        return productRepository.findByDeletedProducts(deletedProducts);
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

    //-----------------------------------------------------------------------------------------------------------
    //Funciones API

    private Product toDomain(ProductDTO productDTO) {
		return mapper.toDomain(productDTO);
	}

	private List<ProductDTO> toDTOs(List<Product> products) {
		return mapper.toDTOs(products);
	}

    private ProductDTO toDTO(Product product) {
		return mapper.toDTO(product);
	}

    public ProductDTO getById(long id) {
        return mapper.toDTO(productRepository.findById(id).get());
    }

    public List<ProductDTO> getAllById(List<Long> productIds) {
        return mapper.toDTOs(productRepository.findAllById(productIds));
    }

    public List<ProductDTO> getByDeleteProducts(Boolean deletedProducts) {
        return mapper.toDTOs(productRepository.findByDeletedProducts(deletedProducts));
    }

    public List<ProductDTO> getAll() {
        return mapper.toDTOs(productRepository.findAll());
    }

    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = toDomain(productDTO);
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ProductDTO saveProduct(ProductDTO productDTO, List<Long> selectedUsers) throws IOException {
        Product product = toDomain(productDTO);
        if (selectedUsers != null) {
            List<User> products = userService.findByIds(selectedUsers);
            product.setUsers(products);
        }
        productRepository.save(product);
        return mapper.toDTO(product);
    }

    public ProductDTO deleteProduct(ProductDTO productDTO) {
        Product product = toDomain(productDTO);
        if (productRepository.existsById(product.getId())) {
            productRepository.delete(product);
        }
        return mapper.toDTO(product);
    }

    public ProductDTO deleteProductById(long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            productRepository.delete(product);
            return mapper.toDTO(product);
        }
        return null;
    }

    public ProductDTO update(Long id, ProductDTO productDTO) {
        Optional<Product> auxOptional = productRepository.findById(id);
        Product aux = auxOptional.orElseThrow(() -> new RuntimeException("Product not found"));
        aux.setName(toDomain(productDTO).getName());
        aux.setDescription(toDomain(productDTO).getDescription());
        aux.setPrice(toDomain(productDTO).getPrice());
        aux.setStock(toDomain(productDTO).getStock());
        aux.setUsers(toDomain(productDTO).getUsers());
        aux.setImageFile(toDomain(productDTO).getImageFile());
        aux.setProvider(toDomain(productDTO).getProvider());
        productRepository.save(aux);
        return mapper.toDTO(aux);
    }

    public void createProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		product.setImg(true);
		product.setImageFile(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void replaceProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		if (!product.getImg()) {
			throw new NoSuchElementException();
		}

		product.setImageFile(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void deleteProductImage(long id) {

		Product product = productRepository.findById(id).orElseThrow();

		if (!product.getImg()) {
			throw new NoSuchElementException();
		}

		product.setImageFile(null);
		product.setImg(false);

		productRepository.save(product);
	}

    public Resource getProductImage(long id) {
        Product product = productRepository.findById(id).orElseThrow();
        if (!product.getImg()){
            throw new NoSuchElementException();
        }
        Blob blob = product.getImageFile();
        try {
            InputStream inputStream = blob.getBinaryStream();
            return new InputStreamResource(inputStream);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving image", e);
        }
    }
}
