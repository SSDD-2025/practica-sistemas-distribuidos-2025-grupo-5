package es.codeurjc.practica1.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.DecoratedWith;
import es.codeurjc.practica1.model.Order;

@Mapper(componentModel = "spring")

public interface OrderMapper {

    OrderDTO toDTO(Order order);

    Order toDomain(OrderDTO orderDTO);

}
