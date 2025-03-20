package es.codeurjc.practica1.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users/")
	public String showUsers(Model model) {

		model.addAttribute("users", userService.findAll());

		return "users";
	}

	@GetMapping("/users/{id}")
	public String showUser(Model model, @PathVariable long id) {

		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			model.addAttribute("user", user.get());
			return "user";
		} else {
			return "users";
		}
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/loginerror")
	public String loginerror() {
		return "loginerror";
	}
	@GetMapping("/private")
	public String privatePage() {
		return "private";
	}
}
