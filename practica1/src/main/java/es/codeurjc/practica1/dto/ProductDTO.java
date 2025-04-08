package es.codeurjc.practica1.dto;
import java.util.List;
    public record ProductDTO(
    Long id,
    String name,
    Double price,
    int stock,
    String provider,

    String description,

    List<UserDTO>users,

    boolean image,

    List<ReviewDTO>reviews,

    List<OrderDTO> orders){}


