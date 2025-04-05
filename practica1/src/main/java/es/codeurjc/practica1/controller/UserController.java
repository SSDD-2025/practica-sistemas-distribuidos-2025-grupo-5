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


	@GetMapping("/users/")
	public String showUsers(Model model, HttpServletRequest request) {
		//-------------
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());
		//-------------
		model.addAttribute("users", userService.findAll());

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
			model.addAttribute("user", user.get());
			return "user";
		} else {
			return "users";
		}
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
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		return "newUser";
	}

	@PostMapping("/saveNewUser")
	public String saveNewUser(
			Model model,
			@RequestParam String name, 
			@RequestParam String email, 
			@RequestParam String encodedPassword, 
			@RequestParam List<String> roles, 
			@RequestParam int phoneNumber
		) throws IOException, SQLException {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (name == null || encodedPassword == null || email== null) {
			return "redirect:/error";
		}
		User user = new User(name, email, encodedPassword, roles, phoneNumber);
		userService.save(user);

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
		model.addAttribute("users", userService.findAll());
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

			user.get().getProducts().clear();
			for (Review review : user.get().getReviews()) {
				review.setAuthor(null);
				reviewService.save(review);
			}

			for (Order order : user.get().getOrders()) {
				order=null;
				orderService.save(order);
			}

			user.get().getProducts().clear();
			userService.save(user.get());

			userService.delete(user.get());
		} else {
			return "redirect:/error";
		}
		
		System.out.println("AQUIII");
		model.addAttribute("isLoggedIn", isLoggedIn);
		model.addAttribute("users", userService.findAll());
		model.addAttribute("products", productService.findAll());
		return "products"; 
	}

}
