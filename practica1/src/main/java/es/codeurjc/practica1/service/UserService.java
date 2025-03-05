package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllById(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User getLoggedUser() {
        return userRepository.findAll().get(0);
    }

    public void addOrder(Order order) {
        User user = getLoggedUser();
        user.setOrder(order);
        userRepository.save(user);
    }
}
