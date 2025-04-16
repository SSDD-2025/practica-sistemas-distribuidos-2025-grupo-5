package sinUtilizar;

import java.util.List;

import es.codeurjc.practica1.dto.OrderDTO;
import es.codeurjc.practica1.dto.ReviewDTO;
import es.codeurjc.practica1.dto.UserDTO;

public record NewProductRequestDTO (
    String name,
    Double price,
    Integer stock,
    String provider,

    String description,

    List<UserDTO>users,

    boolean image,

    List<ReviewDTO>reviews,

    List<OrderDTO> orders){
}
