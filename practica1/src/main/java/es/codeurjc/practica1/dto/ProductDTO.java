package es.codeurjc.practica1.dto;
import java.sql.Blob;
import java.util.List;
    public record ProductDTO(
    Long id,
    String name,
    Double price,
    Integer stock,
    String provider,

    String description,

    //List<UserDTO>users,

    Blob image,

    List<ReviewDTO>reviews

    //List<OrderDTO> orders
    ){}


