package es.codeurjc.practica1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

}
