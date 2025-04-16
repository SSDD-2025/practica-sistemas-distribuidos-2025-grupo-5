package es.codeurjc.practica1.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


//---- USER
    @GetMapping("/me")
    public UserDTO me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER REST CONTROLLER LINE 22"));

        return userMapper.toDTO(user);
    }

    @PutMapping("/me")
    public UserDTO updateMe(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND"));

        user.setName(userDTO.name());  // o los campos que desees actualizar
        user.setEmail(userDTO.email()); // por ejemplo, si tienes un campo de email
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }
    @DeleteMapping("/me")
    public UserDTO deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
    
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    
        user.setDeletedd(true);
        userRepository.save(user);
    
        return userMapper.toDTO(user);
    }
    
//---- ADMIN

    @GetMapping("/admin")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeleted(false);
        return users.stream()
                    .map(userMapper::toDTO)
                    .toList();
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        if (userRepository.findByName(userDTO.name()).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Ya existe un usuario con ese nombre");
        }

        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Ya existe un usuario con ese email");
        }

        User user = userMapper.toDomain(userDTO);
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(savedUser));
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

    @PutMapping("/admin/{id}")
    public UserDTO updateUserById(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

}
