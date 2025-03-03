package es.codeurjc.practica1.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.model.User;
import es.codeurjc.practica1.repositories.UserRepository;

@Service
public class ReviewService {

	
	@Autowired
	private ReviewService reviewRepository;

	@Autowired
	private UserRepository userRepository;

	public List<Review> findAll() {
		return reviewRepository.findAll();
	}

	public Optional<Review> findById(long id) {
		return reviewRepository.findById(id);
	}

	public void save(Review post) {
		reviewRepository.save(post);		
	}

	public void update(Review oldPost, Review updatedPost) {
		oldPost.setTitle(updatedPost.getTitle());
		oldPost.setText(updatedPost.getText());
		reviewRepository.save(oldPost);
	}

	public void deleteById(long id) {
		
	}

	public void delete(Review review) {

		
		Optional<User> user = userRepository.findById(review.getAuthor().getId());
		if(user.isPresent()) {
			User userAux = user.get();
			userAux.deleteReview(review);
			userRepository.save(user.get());
			reviewRepository.delete(review);
		}else{
			System.out.println("User not found");
		}

	}

}
    

