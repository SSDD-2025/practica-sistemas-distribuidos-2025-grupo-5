package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.OrderService;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.ReviewService;
import es.codeurjc.practica1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------

		if(principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/editUserGet")
	public String editUserGet(Model model) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		
		
		User user=userService.findByName(authentication.getName()).get();

		model.addAttribute("user",user);
		return "editUser";
	}

	@PostMapping("/updateUser")
	public String updateUser(@RequestParam String name,
								@RequestParam String email,
								@RequestParam int phoneNumber, Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux=userService.findAll();
		listAux.remove(0);
		model.addAttribute("users",listAux);		
		model.addAttribute("products", productService.findAll());
		
		User user = userService.findByName(authentication.getName()).get();
		user.setName(name);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);

		userService.save(user);
		return "/products"; // Redirige correctamente
	}

/*
	@GetMapping("/users/")
	public String showUsers(Model model, HttpServletRequest request) {
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------
		List<User> listAux=userService.findAll();
		listAux.remove(0);
		model.addAttribute("users",listAux);

		return "users";
	}
 
	@GetMapping("/users/{id}")
	public String showUser(Model model, @PathVariable long id, HttpServletRequest request) {

		Optional<User> user = userService.findById(id);
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------
		if (user.isPresent()) {
			List<User> listAux=
			model.addAttribute("user", user.get());
			return "user";
		} else {
			return "users";
		}
	}*/

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
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------
		return "loginerror";
	}
	
	@GetMapping("/private")
		public String privatePage(Model model, HttpServletRequest request) {
		String name = request.getUserPrincipal().getName();
		User user = userService.findByName(name).orElseThrow();
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------
		model.addAttribute("username", user.getName());
		model.addAttribute("admin", request.isUserInRole("ADMIN"));
		return "private";
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
			@RequestParam int phoneNumber
		) throws IOException, SQLException {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (name == null || name.isEmpty() || encodedPassword == null || email== null) {
			model.addAttribute("message", "El nombre, la contraseña y el email no pueden estar vacíos.");
			return "/error";

		}

		System.out.println("CONTRASEÑA GUARDADA"+encodedPassword);
		String hashedPassword = passwordEncoder.encode(encodedPassword);
		List<String>rol = List.of("USER");
		userService.save(new User(name, email, hashedPassword, rol, phoneNumber));

		if (isLoggedIn) {
			boolean isAdmin = authentication.getAuthorities().stream()
											.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
			
			if (isAdmin) {
				model.addAttribute("isAdmin", isAdmin);
				System.out.println("El usuario es ADMIN");
			} else {
				model.addAttribute("isAdmin", isAdmin);
				System.out.println("El usuario NOOOOO es ADMIN");
			}
		}

		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux=userService.findAll();
		listAux.remove(0);
		model.addAttribute("users",listAux);		
		model.addAttribute("products", productService.findAll());
		//model.addAttribute("userName",user.getName());

		return "products";
	}

	@PostMapping("/removeUser/{id}")
	public String removeUser(Model model,@PathVariable long id) {
		// Search for the product in the database.
		Optional<User> user = userService.findById(id);

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (user.isPresent()) {
			for (Product product : user.get().getProducts()) {
				product.getUsers().remove(user);
				productService.save(product);
			}
			System.out.println("ENTRO EN BORRARME");

			user.get().getProducts().clear();
			for (Review review : user.get().getReviews()) {
				review.removeAllComments();
				review.getAuthor().deleteReview(review);
				review.getProduct().removeReview(review);
				reviewService.delete(review);
			}

			for (Order order : user.get().getOrders()) {
				order.deleteAllProducts();
				order.getOwner().deleteOrder(order);
				orderService.delete(order);
			}

			user.get().getProducts().clear();
			userService.save(user.get());

			userService.delete(user.get());
		} else {
			return "/error";
		}
		
		model.addAttribute("isLoggedIn", isLoggedIn);
		List<User> listAux=userService.findAll();
		listAux.remove(0);
		model.addAttribute("users",listAux);
		model.addAttribute("products", productService.findAll());
		model.addAttribute("isAdmin", true);
		return "products"; 
	}

	@PostMapping("/removeUser")
	public String removeUser(Model model) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Search for the product in the database.
		Optional<User> user = userService.findByName(authentication.getName());

		if (user.isPresent()&&user.get().getId()!=1) {
			for (Product product : user.get().getProducts()) {
				product.getUsers().remove(user);
				productService.save(product);
			}

			user.get().getProducts().clear();
			for (Review review : user.get().getReviews()) {
				review.removeAllComments();
				review.getAuthor().deleteReview(review);
				review.getProduct().removeReview(review);
				reviewService.delete(review);
			}

			for (Order order : user.get().getOrders()) {
				order.deleteAllProducts();
				order.getOwner().deleteOrder(order);
				orderService.delete(order);
			}

			user.get().getProducts().clear();
			userService.save(user.get());

			userService.delete(user.get());
		} else {
			return "/error";
		}
		
		model.addAttribute("isLoggedIn", false);
		model.addAttribute("products", productService.findAll());
		model.addAttribute("isAdmin", false);
		return "redirect:/products"; 
	}
}
