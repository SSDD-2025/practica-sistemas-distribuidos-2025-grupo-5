package es.codeurjc.practica1.controller.rest;

import java.net.URI;
import java.sql.SQLException;
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
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.OrderDTO;
import es.codeurjc.practica1.service.OrderService;

@RestController
@RequestMapping("/api/shops")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public List<OrderDTO> getOrders() {

        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable long id) {

        return orderService.getOrderById(id);
    }

    @PostMapping("/")
    public ResponseEntity<OrderDTO> createShop(@RequestBody OrderDTO orderDTO) {

        orderDTO = orderService.saveOrder(orderDTO);

        //As Shop is related to other entities, we need to reload it to get the related entities
        orderDTO = orderService.getOrderById(orderDTO.id());

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(orderDTO.id()).toUri();

        return ResponseEntity.created(location).body(orderDTO);
    }

    @PutMapping("/{id}")
    public OrderDTO replaceOrder(@PathVariable long id, @RequestBody OrderDTO updatedOrderDTO) throws SQLException {

        return orderService.updateOrder(id, updatedOrderDTO);
    }

    @DeleteMapping("/{id}")
    public OrderDTO deleteOrder(@PathVariable long id) {

        return orderService.deleteOrderById(id);
    }
   
}