package es.codeurjc.practica1.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.practica1.dto.UserDTO;
import es.codeurjc.practica1.dto.UserMapper;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;
import es.codeurjc.practica1.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	
	@Autowired
	private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/me")
    public UserDTO me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER REST CONTROLLER LINE 22"));

        return userMapper.toDTO(user);
    }
}
