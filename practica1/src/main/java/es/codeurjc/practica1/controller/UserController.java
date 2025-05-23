package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());

		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/editUserGet")
	public String editUserGet(Model model, HttpServletRequest request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);

		User user = userService.findByName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		model.addAttribute("isLoggedIn", isLoggedIn);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("user", user);
		return "editUser";
	}

	@PostMapping("/updateUser")
	public String updateUser(@RequestParam String name,
			@RequestParam String email,
			@RequestParam int phoneNumber,
			Model model, HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		User user = userService.findByName(authentication.getName())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		user.setName(name);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		userService.save(user);

		UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(name);

		Authentication newAuth = new UsernamePasswordAuthenticationToken(
				updatedUserDetails,
				authentication.getCredentials(),
				updatedUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);

		List<User> listAux = userService.findByDeleted(false);
		if (!listAux.isEmpty()) {
			listAux.remove(0);
		}
		if (newAuth.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
			model.addAttribute("isAdmin", true);
		} else {
			model.addAttribute("isAdmin", false);
		}
		model.addAttribute("users", listAux);
		model.addAttribute("products", productService.findByDeleteProducts(false));
		return "/products";
	}

	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);

		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		model.addAttribute("isLoggedIn", isLoggedIn);
		return "login";
	}

	@GetMapping("/loginerror")
	public String loginerror(Model model, HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		return "loginerror";
	}

	@GetMapping("/newUser")
	public String newUser(Model model) {

		model.addAttribute("isLoggedIn", false);
		model.addAttribute("isAdmin", false);

		return "newUser";
	}

	@PostMapping("/saveNewUser")
	public String saveNewUser(
			Model model,
			@RequestParam String name,
			@RequestParam String email,
			@RequestParam String encodedPassword,
			@RequestParam int phoneNumber, HttpServletRequest request)
			throws IOException, SQLException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());

		List<User> aux = userService.findAll();
		if (name == null || name.isEmpty() || encodedPassword == null || email == null) {
			model.addAttribute("isLoggedIn", false);
			model.addAttribute("isAdmin", false);
			model.addAttribute("message", "El nombre, la contraseña y el email no pueden estar vacíos.");
			request.logout();
			return "/error";

		} else if (userService.findByName(name).isPresent() && aux.contains(userService.findByName(name).get())) {
			model.addAttribute("message", "Este nombre de usuario ya esta cogido, elige otro :)");
			model.addAttribute("isLoggedIn", false);
			model.addAttribute("isAdmin", false);
			request.logout();
			return "/error";
		} else {
			String hashedPassword = passwordEncoder.encode(encodedPassword);
			List<String> rol = List.of("USER");
			userService.save(new User(name, email, hashedPassword, rol, phoneNumber));
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

			model.addAttribute("isAdmin", isAdmin);
			List<User> listAux = userService.findByDeleted(false);
			listAux.remove(0);
			boolean isLoggedIn;
			if (isAdmin) {
				isLoggedIn = true;
			} else {
				isLoggedIn = false;
			}
			model.addAttribute("isLoggedIn", isLoggedIn);
			model.addAttribute("users", listAux);
			model.addAttribute("products", productService.findByDeleteProducts(false));

			return "products";
		}
	}

	@PostMapping("/removeUser/{id}")
	public String removeUser(Model model, @PathVariable long id, HttpServletRequest request) {
		
		Optional<User> user = userService.findById(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		user.get().setDeletedd(true);
		userService.save(user.get());
		model.addAttribute("isLoggedIn", true);
		model.addAttribute("products", productService.findByDeleteProducts(false));
		model.addAttribute("isAdmin", true);

		return "redirect:/";
	}

	@PostMapping("/removeUserByUser")
	public String removeUser(Model model, HttpServletRequest request, HttpServletResponse response) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getName();

		User user = userService.findByName(username).get();
		user.setDeletedd(true);
		userService.save(user);

		model.addAttribute("isLoggedIn", false);
		model.addAttribute("products", productService.findByDeleteProducts(false));
		model.addAttribute("isAdmin", false);

		return "/logout";
	}

}
