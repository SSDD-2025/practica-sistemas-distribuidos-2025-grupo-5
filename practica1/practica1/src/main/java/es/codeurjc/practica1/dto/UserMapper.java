package es.codeurjc.practica1.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.practica1.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    
    User toDomain(UserDTO userDTO);
    

}
