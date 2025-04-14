package es.codeurjc.practica1.dto;

import java.util.List;

public record ReviewDTO (
    Long id,
    String tittle,
    String text,
    List<String> comments,
    UserDTO author,
    ProductDTO product){}
