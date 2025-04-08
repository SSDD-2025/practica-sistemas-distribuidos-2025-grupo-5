package es.codeurjc.practica1.dto;

import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.Order;

public interface OrderMapper {

    OrderDTO toDTO(Order order);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "products", ignore = true)
    Order toDomain(OrderDTO orderDTO);

}
