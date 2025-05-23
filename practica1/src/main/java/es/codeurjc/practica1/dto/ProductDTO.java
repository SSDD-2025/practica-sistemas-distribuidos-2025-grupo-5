package es.codeurjc.practica1.dto;

import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        Double price,
        Integer stock,
        String provider,

        String description,

        String image,

        List<ReviewDTO> reviews

) {
}
