package es.codeurjc.practica1.dto;
import java.util.List;

public record UserDTO (
    Long id,
    String name,
    String email,
    String password,
    List<String> roles,
    List<ReviewDTO> reviews,
    List<ProductDTO> products,
    List<OrderDTO> orders,
    Integer phoneNumber,
    boolean image) {}
