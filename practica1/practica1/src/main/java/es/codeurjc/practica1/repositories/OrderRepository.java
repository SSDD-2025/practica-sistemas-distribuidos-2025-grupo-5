package es.codeurjc.practica1.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(long id);
}
