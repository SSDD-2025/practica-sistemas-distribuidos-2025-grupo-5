package es.codeurjc.practica1.dto;

import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.Review;

public interface ReviewsMapper {

    ReviewDTO toDTO(Review review);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "products", ignore = true)
    Review toDomain(ReviewDTO reviewDTO);

}
