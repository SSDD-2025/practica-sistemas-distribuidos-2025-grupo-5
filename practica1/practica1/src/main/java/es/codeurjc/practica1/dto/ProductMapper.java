package es.codeurjc.practica1.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

@Mapping(target = "reviews", source = "reviews")
    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOs(List<Product> products);

    @Mapping(target = "reviews", ignore = true)
    Product toDomain(ProductDTO productDTO);
}