package es.codeurjc.practica1.dto;

import java.util.List;

public record OrderDTO (
    Long id,
    Double totalPrice,
    UserDTO owner,
    List<ProductDTO> products){}
