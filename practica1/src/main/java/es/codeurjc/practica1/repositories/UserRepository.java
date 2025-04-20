package es.codeurjc.practica1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findById(long id);

    List<User> findAll();

    List<User> findByDeleted(boolean deleted);

    Optional<User> findByEmail(String email);

}
