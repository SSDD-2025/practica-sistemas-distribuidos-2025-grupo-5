package es.codeurjc.practica1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    
    @Override
    public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> roles = new ArrayList<>();

        for (String role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
            return new org.springframework.security.core.userdetails.User(user.getName(),
        user.getPassword(), roles);
    }

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

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public void addOrder(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.addOrder(order);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void removeOrderFromUser(Long userId, Order order) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.deleteOrder(order);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Order> getOrdersByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(User::getOrders).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
