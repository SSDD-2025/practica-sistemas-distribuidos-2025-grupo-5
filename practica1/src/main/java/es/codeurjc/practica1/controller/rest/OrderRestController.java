package es.codeurjc.practica1.controller.rest;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.OrderDTO;
import es.codeurjc.practica1.dto.OrderMapper;
import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.service.OrderService;

@RestController
@RequestMapping("/api/shops")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;


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
    public ResponseEntity<OrderDTO> createShop(@RequestBody OrderDTO orderDTO) {
        Order newOrder = orderMapper.toDomain(orderDTO);
        orderService.save(newOrder);
        Order createdOrder = orderService.findById(newOrder.getId()).get();
        OrderDTO responseDTO = orderMapper.toDTO(createdOrder);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{id}")
    public OrderDTO replaceOrder(@PathVariable long id, @RequestBody OrderDTO updatedOrderDTO) throws SQLException {
        Order originalOrder = orderService.findById(id).get();
        Order updatedOrder = orderMapper.toDomain(updatedOrderDTO);
        orderService.update(originalOrder, updatedOrder);
        return orderMapper.toDTO(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public OrderDTO deleteOrder(@PathVariable long id) {
        Order orderToDelete = orderService.findById(id).get();
        orderService.delete(orderToDelete);
        return orderMapper.toDTO(orderToDelete);
    }
   
}


    