package es.codeurjc.practica1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(long id);

    List<Product> findByName(String name);

    List<Product> findByDeletedProducts(boolean deletedProducts);

    Page<Product> findByDeletedProducts(Pageable pageable, boolean deletedProducts);

    List<Product> findByProvider(String provider);

    List<Product> findByPriceLessThan(double price);

    List<Product> findByPriceGreaterThan(double price);

    List<Product> findByPriceEquals(double price);

    boolean existsById(long id);

    List<Product> findAll();

    Product save(Product product);

}
