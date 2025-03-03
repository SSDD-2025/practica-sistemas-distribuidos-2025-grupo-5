package es.codeurjc.practica1.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
