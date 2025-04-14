package es.codeurjc.practica1.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.Order;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOs(List<Order> orders);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "products", ignore = true)
    Order toDomain(OrderDTO orderDTO);

}
