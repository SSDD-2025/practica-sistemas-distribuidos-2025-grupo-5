package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.dto.ReviewDTO;
import es.codeurjc.practica1.dto.ReviewsMapper;
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

	@Autowired
	private UserService userService;

	@Autowired
	private ReviewsMapper mapper;

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
		if (user.isPresent()) {
			User userAux = user.get();
			userAux.deleteReview(review);
			userRepository.save(userAux);
			reviewRepository.delete(review);
		} else {
			System.out.println("User not found");
		}
	}


	//-----------------------------------------------------------------------
    // Funciones API

    private Review toDomain(ReviewDTO reviewDTO) {
		return mapper.toDomain(reviewDTO);
	}

	private List<ReviewDTO> toDTOs(List<Review> reviews) {
		return mapper.toDTOs(reviews);
	}

    private ReviewDTO toDTO(Review review) {
		return mapper.toDTO(review);
	}

	public List<ReviewDTO> getAll() {
		return mapper.toDTOs(reviewRepository.findAll());
	}

	public ReviewDTO getById(long id) {
		return mapper.toDTO(reviewRepository.findById(id).get());
	}

	public ReviewDTO saveReview(ReviewDTO reviewDTO) {
		Review review = toDomain(reviewDTO);
		reviewRepository.save(review);
		return mapper.toDTO(review);
	}

	public ReviewDTO updateReview(ReviewDTO oldReview, ReviewDTO updatedReview) {
		Review aux = toDomain(oldReview);
		aux.setTitle(toDomain(updatedReview).getTitle());
		aux.setText(toDomain(updatedReview).getText());
		reviewRepository.save(aux);
		return mapper.toDTO(aux);
	}

	public ReviewDTO deleteReview(ReviewDTO reviewDTO) {
		Review review = toDomain(reviewDTO);
		Optional<User> user = userRepository.findById(review.getAuthor().getId());
		if (user.isPresent()) {
			User userAux = user.get();
			userAux.deleteReview(review);
			userRepository.save(userAux);
			reviewRepository.delete(review);
			return mapper.toDTO(review);
		} else {
			throw new RuntimeException("User not found");
		}
	}

}
