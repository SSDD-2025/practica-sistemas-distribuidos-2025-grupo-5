package es.codeurjc.practica1.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

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
		
		CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
		model.addAttribute("token", token.getToken());

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

}
