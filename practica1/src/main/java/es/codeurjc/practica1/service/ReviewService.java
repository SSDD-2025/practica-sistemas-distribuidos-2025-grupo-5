package es.codeurjc.practica1.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.ReviewRepository;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserRepository userRepository;

	public List<Review> findAll() {
		return reviewRepository.findAll();
	}

	public Optional<Review> findById(long id) {
		return reviewRepository.findById(id);
	}

	public void save(Review review) {
		reviewRepository.save(review);		
	}

	public void update(Review oldReview, Review updatedReview) {
		oldReview.setTitle(updatedReview.getTitle());
		oldReview.setText(updatedReview.getText());
		reviewRepository.save(oldReview);
	}

	public void delete(Review review) {
		Optional<User> user = userRepository.findById(review.getAuthor().getId());
		if(user.isPresent()) {
			User userAux = user.get();
			userAux.deleteReview(review);
			userRepository.save(userAux);
			reviewRepository.delete(review);
		} else {
			System.out.println("User not found");
		}
	}
}

    

