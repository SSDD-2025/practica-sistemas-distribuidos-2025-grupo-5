package es.codeurjc.practica1.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.practica1.model.Order;
import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.OrderService;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@GetMapping("/showOrders")
	public String showOrders(HttpSession session, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
		
		model.addAttribute("isLoggedIn", isLoggedIn);
		if (userDetails == null) {
			return "/login";
		}

		// Get the list of product IDs in the session.
		Optional<User> oneUser = userService.findByName(userDetails.getUsername());
		List<Order> orderList = null;

		if (oneUser.isPresent()) {
			User user = oneUser.get();
			orderList = user.getOrders();
		}

		if (orderList.isEmpty()) {
			model.addAttribute("message", "No tienes ningún pedido de momento");
		} else {
			model.addAttribute("orderList", orderList);
		}

		return "orders"; // Display the cart view.
	}

	@GetMapping("/checkout")
	public String showGateway(HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());
		Order order;
		if (user != null) {
			User userAux = user.get();
			List<Product> cartProduct = userAux.getProducts();

			if (cartProduct.size() == 1) {
				order = new Order(userAux, cartProduct.get(0));
				order.setTotalPrice(cartProduct.get(0).getPrice());
				cartProduct.get(0).setStock(cartProduct.get(0).getStock() - 1);
				productService.save(cartProduct.get(0));
				cartProduct.get(0).setOrder(order);

			} else {

				order = new Order(userAux, cartProduct.get(0));
				for (int i = 1; i < cartProduct.size(); i++) {
					Product product = cartProduct.get(i);
					if (product.getStock() > 0) {
						order.addProduct(product);

						order.setTotalPrice(order.getTotalPrice() + cartProduct.get(i).getPrice());
						product.setStock(product.getStock() - 1);

						product.setOrder(order);
						productService.save(product);

					} else {
						model.addAttribute("message", "El producto " + product.getName() + " no está disponible");
						throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
					}
				}
			}

			orderService.save(order);
			user.get().getProducts().clear();
			userService.addOrder(user.get().getId(), order);
			userService.save(user.get());
			model.addAttribute("orders", order);

			boolean isLoggedIn = authentication != null &&
					authentication.isAuthenticated() &&
					!(authentication instanceof AnonymousAuthenticationToken);
			model.addAttribute("isLoggedIn", isLoggedIn);
		}
		return "/gateway";
	}

	@PostMapping("/removeOrder/{id}")
	public String removeOrder(@PathVariable Long id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> userA = userService.findByName(authentication.getName());
		Optional<Order> order = orderService.findById(id);

		try {
			if (order != null && userA.get().getOrders().contains(order.get())) {

				for (Product product : order.get().getProducts()) {
					product.setStock(product.getStock() + 1);
					productService.save(product);
				}

				User user = userA.get();
				user.deleteOrder(order.get());

				userService.save(user);
				orderService.delete(order.get());

				return "redirect:/";
			} else {
				return "redirect:/error";
			}

		} catch (Exception e) {
			return "redirect:/error";
		}
	}
}
