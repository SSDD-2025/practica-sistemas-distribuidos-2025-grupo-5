package es.codeurjc.practica1.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.ProductRepository;
import es.codeurjc.practica1.repositories.OrderRepository;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void update(Order oldOrder, Order updatedOrder) {
        oldOrder.setProducts(updatedOrder.getProducts());
        oldOrder.setTotalPrice(updatedOrder.getTotalPrice());
        orderRepository.save(oldOrder);
    }

    public void delete(Order order) {
        Optional<User> user = userRepository.findById(order.getAuthor().getId());
        if (user.isPresent()) {
            User userAux = user.get();
            userAux.deleteOrder(order);
            userRepository.save(userAux);
            orderRepository.delete(order);
        } else {
            System.out.println("User not found");
        }
    }
}
