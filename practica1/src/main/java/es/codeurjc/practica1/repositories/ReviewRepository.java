package es.codeurjc.practica1.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
