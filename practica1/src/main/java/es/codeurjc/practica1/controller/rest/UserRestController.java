package es.codeurjc.practica1.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

//update user does not exist yet
//---- USER
    @GetMapping("/me")
    public UserDTO me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER REST CONTROLLER LINE 22"));

        return userMapper.toDTO(user);
    }
//---- ADMIN

    @GetMapping("/admin/")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeleted(false);
        return users.stream()
                    .map(userMapper::toDTO)
                    .toList();
    }

    @GetMapping("/admin/user/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("USER REST CONTROLLER LINE 30"));
        return userMapper.toDTO(user);
    }

    @DeleteMapping("/admin/user/{id}")
    public UserDTO deleteUser(@PathVariable long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setDeletedd(true);
        userService.save(user);
        return userMapper.toDTO(user);
    }
}
