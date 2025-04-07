package es.codeurjc.practica1.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.OrderService;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.ReviewService;
import es.codeurjc.practica1.service.UserService;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.servlet.http.HttpSession;

public class ReviewController {
    
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

	// Reviwes
	@GetMapping("/productReviews/{id}")
	public String showReviews(Model model, @PathVariable long id) {
		System.err.println("ENTRA EN SHOW REVIEWS");

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		
		if (isLoggedIn) {
			//tiene que ser asi porque puede ser que te de como válido un usuario anónimo
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
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			Product p = product.get();

			model.addAttribute("reviews", p.getReviews());
			return "reviews";
		} else {
			return "/error";
		}
	}

	@GetMapping("/newReview/{productId}")
	public String newReview(@PathVariable long productId, Model model) {
		Optional<Product> productOpt = productService.findById(productId);

		if (!productOpt.isPresent()) {
			return "/error";
		}
		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);
		//-----
		model.addAttribute("product", productOpt.get());
		model.addAttribute("productId", productId);
		return "newReview";
	}

	@PostMapping("/newReview/{productId}")
	public String newReviewProcess(
			@PathVariable long productId,
			@RequestParam String title,
			@RequestParam String text) {

		System.out.println("ENTRA EN NEW REVIEW");

		Optional<Product> productOpt = productService.findById(productId);
		if (!productOpt.isPresent()) {
			return "/error";
		}
		Product product = productOpt.get();

		Optional<User> userOpt = userService.findByEmail("paula@gmail.com");
		User author = userOpt.get();

		Review review = new Review(title, text, author, product);
		product.addReview(review);

		reviewService.save(review);
		productService.save(product);
		return "/productReviews/" + product.getId();
	}

	@PostMapping("/removeReview/{reviewId}")
	public String removeReview(@PathVariable long reviewId, HttpSession session, Model model) {
		try {
			List<Review> reviews = (List<Review>) session.getAttribute("reviews");

			Optional<Review> reviewAux = reviewService.findById(reviewId);
			if (!reviewAux.isPresent()) {
				return "/reviews";
			}

			Review review = reviewAux.get();
			User userAux = userService.findById(review.getAuthor().getId()).orElse(null);
			Product productAux = productService.findById(review.getProduct().getId()).orElse(null);

			if (userAux == null || productAux == null) {
				return "/reviews";
			}

			userAux.deleteReview(review);
			productAux.removeReview(review);

			userService.save(userAux);
			productService.save(productAux);

			reviewService.delete(review);

			if (reviews != null) {
				reviews.removeIf(r -> r.getId() == reviewId);
			} else {
				reviews = new ArrayList<>();
			}

			session.setAttribute("reviews", reviews);
			List<Review> updatedReviews = reviewService.findAll();

			session.setAttribute("reviews", updatedReviews);
			return "/productReviews/" + productAux.getId();
		} catch (Exception e) {
			return "/error";
		}
	}

	@GetMapping("/reviews/{productId}")
	public String showReviews(@PathVariable Long productId, Model model, HttpSession session) {
		try {
			Optional<Product> productOpt = productService.findById(productId);
			if (!productOpt.isPresent()) {
				return "/error";
			}

			Product product = productOpt.get();
			List<Review> reviews = product.getReviews();

			session.setAttribute("reviews", reviews);
			model.addAttribute("reviews", reviews);
			
			//TOOLBAR
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean isLoggedIn = authentication != null &&
			authentication.isAuthenticated() &&
			!(authentication instanceof AnonymousAuthenticationToken);
			model.addAttribute("isLoggedIn", isLoggedIn);
			//-----
			return "reviews";

		} catch (Exception e) {
			return "/error";
		}
	}
}
