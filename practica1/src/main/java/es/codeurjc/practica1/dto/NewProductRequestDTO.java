package es.codeurjc.practica1.dto;

import java.util.List;

public record NewProductRequestDTO (
    String name,
    Double price,
    Integer stock,
    String provider,

    String description,

    List<UserDTO>users,

    boolean image,

    List<ReviewDTO>reviews,

    List<OrderDTO> orders){
}
