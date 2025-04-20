package es.codeurjc.practica1.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.practica1.dto.UserDTO;
import es.codeurjc.practica1.dto.UserMapper;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;
import es.codeurjc.practica1.security.jwt.AuthResponse;
import es.codeurjc.practica1.security.jwt.AuthResponse.Status;
import es.codeurjc.practica1.security.jwt.LoginRequest;
import es.codeurjc.practica1.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

	@Autowired
	private UserLoginService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {

		return userService.login(response, loginRequest);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userService.refresh(response, refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
	}

	@PostMapping("/register")
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
}