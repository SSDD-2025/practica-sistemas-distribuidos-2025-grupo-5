package es.codeurjc.practica1.controller;

import java.util.ArrayList;
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
import es.codeurjc.practica1.service.ReviewService;
import es.codeurjc.practica1.service.UserService;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ImageUtils imageUtils;
	@Autowired
	private ReviewService reviewService;


	@GetMapping("/showOrders")
	public String showOrders(HttpSession session, Model model,@AuthenticationPrincipal UserDetails userDetails) {
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//----
		System.out.println("USUARIO ESTA EN EL SHOW ORDERS");

		if (userDetails == null) {
			return "/login"; // o manejar el caso de usuario no autenticado
		}

		// Get the list of product IDs in the session.
		Optional<User> oneUser = userService.findByName(userDetails.getUsername());
		List<Order> orderList = null;
		System.out.println("USUARIO ESTA EN EL SHOW ORDERS");
		
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


	// ORDER
	@GetMapping("/checkout")
	public String showGateway( HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = userService.findByName(authentication.getName());
		Order order;
			if (user != null) {
				User userAux=user.get();
				List<Product> cartProduct= userAux.getProducts();
				
				if(cartProduct.size()==1){
					order= new Order(userAux, cartProduct.get(0));
					order.setTotalPrice(cartProduct.get(0).getPrice());
					cartProduct.get(0).setStock(cartProduct.get(0).getStock() - 1);
					productService.save(cartProduct.get(0));
					cartProduct.get(0).setOrder(order);
					model.addAttribute("product", cartProduct.get(0));

				}else{

					order= new Order(userAux, cartProduct.get(0));
					for (int i = 1; i < cartProduct.size(); i++) {
						Product product =cartProduct.get(i);
	
						if (product.getStock() > 0) {
							order.addProduct(product);
							order.setTotalPrice(order.getTotalPrice()+cartProduct.get(i).getPrice());
							product.setStock(product.getStock() - 1);
							product.setOrder(order);
							productService.save(product);
							model.addAttribute("product", product);
	
						} else {
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
						}
					}
				}
				
				orderService.save(order);
				//System.out.println("Order id (checkoutOne) ="+order.getId());

				userService.addOrder(user.get().getId(), order);
				userService.save(user.get());
				model.addAttribute("orders", order);
				//System.out.println("ORDER ID ANTES GATEWAY EN CHECK OUT"+order.getId());
				
				//TOOLBAR
				boolean isLoggedIn = authentication != null &&
				authentication.isAuthenticated() &&
				!(authentication instanceof AnonymousAuthenticationToken);
				model.addAttribute("isLoggedIn", isLoggedIn);
				//-----
			}
			return "/gateway";
	}

	@GetMapping("/checkoutOne/{id}")
	public String showGatewayOne(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, HttpSession session, Model model) {
		// Get the list of product IDs in the session.
		Optional<Product> productOptional = productService.findById(id);

		//System.out.println(SecurityContextHolder.getContext().getAuthentication());
		
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----

		if (productOptional.isPresent()) {
			Product product = productOptional.get();

			if (product.getStock() > 0) {

				//System.out.println("PRODUCT EXISTE");
				product.setStock(product.getStock() - 1);
				List<Product> cartProducts = new ArrayList<>();
				cartProducts.add(product);
				Optional<User> optionalUser = userService.findByName(userDetails.getUsername());

				if (optionalUser.isEmpty()) {
					//System.out.println("USER NULO");
					return "/error";
				} else {

					Order order = new Order(optionalUser.get(), cartProducts);
					order.setTotalPrice(product.getPrice());

					orderService.save(order);
					System.out.println("Order id (checkoutOne) ="+order.getId());

					userService.addOrder(optionalUser.get().getId(), order);
					userService.save(optionalUser.get());

					product.setOrder(order);
					productService.save(product);
					model.addAttribute("orders", order);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product out of stock");
			}

		} else {
			return "/error";
		}
		return "/gateway";
	}

	@PostMapping("/removeOrder/{id}")
	public String removeOrder(@PathVariable Long id) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> userA = userService.findByName(authentication.getName());
		Optional<Order> order = orderService.findById(id);


		try {
			if (order!=null) {

				for (Product product : order.get().getProducts()) {
					product.setStock(product.getStock() + 1);
					productService.save(product);
				}
				
				User user = userA.get();
				user.deleteOrder(order.get());  //Elimina de la lista de órdenes
				order.get().deleteAllProducts();  // Limpia productos si es necesario

				userService.save(user);  // Guarda cambios en el usuario
				orderService.delete(order.get());  // Ahora sí borra la orden
		
				return "/";
			} else {
				System.out.println("Orden no encontrada");
				return "/error";
			}
		
		} catch (Exception e) {
			System.out.println("SALTA EXCEPCIÓN: " + e.getMessage());
			return "/error";
		}
	}
}
