package es.codeurjc.practica1.controller.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.OrderDTO;
import es.codeurjc.practica1.dto.OrderMapper;
import es.codeurjc.practica1.dto.ProductMapper;
import es.codeurjc.practica1.dto.UserMapper;
import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.ProductRepository;
import es.codeurjc.practica1.repositories.UserRepository;
import es.codeurjc.practica1.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public List<OrderDTO> getOrders() {
        List<OrderDTO> orders = new ArrayList<>();
        for (Order order : orderService.findAll()) {
            orders.add(orderMapper.toDTO(order));
        }
        return orders;
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable long id) {
        Order orderO = orderService.findById(id).get();
        OrderDTO orderD = orderMapper.toDTO(orderO);
        return orderD;
    }

    @PostMapping("/")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
    User owner = userRepository.findById(orderDTO.owner().id())
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<Product> products = orderDTO.products().stream()
        .map(dto -> productRepository.findById(dto.id())
            .orElseThrow(() -> new RuntimeException("Product not found")))
        .toList();

    Order newOrder = new Order();
    newOrder.setTotalPrice(orderDTO.totalPrice());
    newOrder.setOwner(owner);
    newOrder.setProducts(products);

    orderService.save(newOrder);
        Order createdOrder = orderService.findById(newOrder.getId()).get();
        OrderDTO responseDTO = orderMapper.toDTO(createdOrder);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable long id) {
        Optional<Order> orderOpt = orderService.findById(id);
    
        if (orderOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id " + id);
        }
        Order orderToDelete = orderOpt.get();
        OrderDTO dto = orderMapper.toDTO(orderToDelete);
        orderService.delete(orderToDelete);
        return ResponseEntity.ok(dto);
    }
    

   
}


    