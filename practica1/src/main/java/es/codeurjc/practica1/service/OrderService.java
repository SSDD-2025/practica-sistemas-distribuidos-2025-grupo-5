package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.dto.OrderDTO;
import es.codeurjc.practica1.dto.OrderMapper;
import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.OrderRepository;
import es.codeurjc.practica1.repositories.ProductRepository;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderMapper mapper;

    //Obtener todos los pedidos
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    //Buscar un pedido por ID
    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    //Guardar un pedido en la base de datos
    public void save(Order order) {
        orderRepository.save(order);
    }

    //Actualizar un pedido existente con nuevos datos
    public void update(Order oldOrder, Order updatedOrder) {
        oldOrder.setProducts(updatedOrder.getProducts());
        oldOrder.setTotalPrice(updatedOrder.getTotalPrice());
        orderRepository.save(oldOrder);
    }

    //Eliminar un pedido asegurando que se elimine de la lista del usuario
    public void delete(Order order) {
        Optional<User> userOptional = userRepository.findById(order.getOwner().getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.deleteOrder(order);
            userRepository.save(user);
            orderRepository.delete(order);
        } else {
            throw new RuntimeException("User not found, cannot delete order");
        }
    }

    //Obtener todos los pedidos de un usuario específico
    public List<Order> findOrdersByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(User::getOrders).orElseThrow(() -> new RuntimeException("User not found"));
    }

    //-----------------------------------------------------------------------
    // Funciones API

     private Order toDomain(OrderDTO orderDTO) {
		return mapper.toDomain(orderDTO);
	}

	private List<OrderDTO> toDTOs(List<Order> orders) {
		return mapper.toDTOs(orders);
	}

    private OrderDTO toDTO(Order order) {
		return mapper.toDTO(order);
	}

    public List<OrderDTO> getOrdersByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return toDTOs(userOptional.map(User::getOrders).orElseThrow(() -> new RuntimeException("User not found")));
    }

    //Obtener todos los pedidos
    public List<OrderDTO> getAll() {
        return mapper.toDTOs(orderRepository.findAll());
    }

    //Buscar un pedido por ID
    public OrderDTO getOrderById(long id) {
        return mapper.toDTO(orderRepository.findById(id).get());
    }

    //Guardar un pedido en la base de datos
    public OrderDTO saveOrder(OrderDTO orderDTO) {
        Order order = toDomain(orderDTO);
        orderRepository.save(order);
        return mapper.toDTO(order);
    }

    //Actualizar un pedido existente con nuevos datos
    public OrderDTO update(OrderDTO oldOrder, OrderDTO updatedOrder) {
        Order aux = toDomain(oldOrder);
        aux.setProducts(toDomain(updatedOrder).getProducts());
        aux.setTotalPrice(toDomain(updatedOrder).getTotalPrice());
        orderRepository.save(aux);
        return mapper.toDTO(aux);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Optional<Order> auxOptional = orderRepository.findById(id);
        Order aux = auxOptional.orElseThrow(() -> new RuntimeException("Product not found"));
        aux.setOwner(toDomain(orderDTO).getOwner());
        aux.setProducts(toDomain(orderDTO).getProducts());
        aux.setTotalPrice(toDomain(orderDTO).getTotalPrice());
        orderRepository.save(aux);
        return mapper.toDTO(aux);
    }

    //Eliminar un pedido asegurando que se elimine de la lista del usuario
    public OrderDTO deleteOrder(OrderDTO orderDTO) {
        Order order = toDomain(orderDTO);
        Optional<User> userOptional = userRepository.findById(order.getOwner().getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.deleteOrder(order);
            userRepository.save(user);
            orderRepository.delete(order);
            return mapper.toDTO(order);
        } else {
            throw new RuntimeException("User not found, cannot delete order");
        }
    }

    public OrderDTO deleteOrderById(long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            orderRepository.delete(order);
            return mapper.toDTO(order);
        }
        return null;
    }
}
