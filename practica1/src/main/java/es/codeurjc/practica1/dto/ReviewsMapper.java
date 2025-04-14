package es.codeurjc.practica1.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.Review;


@Mapper(componentModel = "spring")
public interface ReviewsMapper {

    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(List<Review> reviews);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "product", ignore = true)
    Review toDomain(ReviewDTO reviewDTO);

}
