package es.codeurjc.practica1.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.practica1.model.Product;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.service.ProductService;
import es.codeurjc.practica1.service.ReviewService;
import es.codeurjc.practica1.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewController {
    @Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;


	@Autowired
	private ReviewService reviewService;

    // Reviwes
	@GetMapping("/productReviews/{id}")
	public String showReviews(Model model, @PathVariable long id) {

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
			
			
			model.addAttribute("isAdmin", isAdmin);
			
		}
		User user= userService.findByName(authentication.getName()).get();
		List<Review> reviews=reviewService.findAll();
			for (Review review: reviews){

				if(review.getAuthor().getName().equals(user.getName())){
					review.setme(true);
					System.err.println("entraaaaaaaaa");
				}else{
					review.setme(false);
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
			@RequestParam String text, Model model) {

		//TOOLBAR
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null &&
		authentication.isAuthenticated() &&
		!(authentication instanceof AnonymousAuthenticationToken);
		model.addAttribute("isLoggedIn", isLoggedIn);

		Optional<Product> productOpt = productService.findById(productId);
		if (!productOpt.isPresent()) {
			return "redirect:/error";
		}

		Product product = productOpt.get();
		Optional<User> userOpt = userService.findByName(authentication.getName());
		User author = userOpt.get();

		Review review = new Review(title, text, author, product);
		author.addReview(review);
		product.addReview(review);
		userService.save(author);
		reviewService.save(review);
		productService.save(product);
		return "redirect:/productReviews/" + product.getId();
	}

	@PostMapping("/removeReview/{reviewId}")
	public String removeReview(@PathVariable long reviewId, HttpSession session, Model model) {
		try {
			// Obtener el usuario autenticado
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// Buscar la review
			Optional<Review> reviewAux = reviewService.findById(reviewId);
			if (!reviewAux.isPresent()) {
				return "/reviews";
			}
			Review review = reviewAux.get();

			// Continuar con la lógica de borrado
			List<Review> reviews = (List<Review>) session.getAttribute("reviews");

			User userAux = userService.findById(review.getAuthor().getId()).orElse(null);
			Product productAux = productService.findById(review.getProduct().getId()).orElse(null);

			if (userAux == null || productAux == null) {
				return "redirect:/reviews";
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
			return "redirect:/productReviews/" + productAux.getId();

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
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			boolean isLoggedIn = authentication != null &&
			authentication.isAuthenticated() &&
			!(authentication instanceof AnonymousAuthenticationToken);

			Product product = productOpt.get();
			List<Review> reviews = product.getReviews();
			User user= userService.findByName(authentication.getName()).get();
			for (Review review: reviews){
				if(review.getAuthor().equals(user)){
					review.setme(true);
				}
			}

			session.setAttribute("reviews", reviews);
			model.addAttribute("reviews", reviews);
			
			

			model.addAttribute("isLoggedIn", isLoggedIn);
			//-----
			return "reviews";

		} catch (Exception e) {
			return "/error";
		}
	}
}
