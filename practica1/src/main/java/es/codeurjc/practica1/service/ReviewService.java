package es.codeurjc.practica1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.dto.ReviewDTO;
import es.codeurjc.practica1.dto.ReviewsMapper;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.repositories.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ReviewRepository ReviewRepository;

	 @Autowired
	private ReviewsMapper mapper;

     private Review toDomain(ReviewDTO reviewDTO) {
		return mapper.toDomain(reviewDTO);
	}

	private List<ReviewDTO> toDTOs(List<Review> reviews) {
		return mapper.toDTOs(reviews);
	}

    private ReviewDTO toDTO(Review review) {
		return mapper.toDTO(review);
	}

	public List<ReviewDTO> findAll() {
		return toDTOs(reviewRepository.findAll());
	}

	public ReviewDTO findById(long id) {
		return toDTO(reviewRepository.findById(id).orElseThrow());
	}

	public void save(ReviewDTO reviewDTO) {

		Review review = toDomain(reviewDTO);
		reviewRepository.save(review);
		//return toDTO(review);
	}

	public void update(ReviewDTO oldReview, ReviewDTO updatedReview) {
		Review AuxReview = toDomain(oldReview);
		AuxReview.setAuthor(toDomain(updatedReview).getAuthor());
		AuxReview.setTitle(toDomain(updatedReview).getTitle());
		AuxReview.setText(toDomain(updatedReview).getText());

		reviewRepository.save(AuxReview);
		//return toDTO(AuxReview);
	}

	public void delete(ReviewDTO reviewDTO) {
		Review review = toDomain(reviewDTO);
		if (review != null && reviewRepository.existsById(review.getId())) {
			reviewRepository.delete(review);
		} else {
			System.out.println("Review not found");
		}
		//return toDTO(review);
	}

	public void deleteReview(Review review) {
		
		if (review != null && reviewRepository.existsById(review.getId())) {
			reviewRepository.delete(review);
		} else {
			System.out.println("Review not found");
		}
	}

	public void saveReview(Review review) {
		reviewRepository.save(review);
	}

	public List<Review> findAllReviews() {
		return reviewRepository.findAll();
	}

	public Optional<Review> findReviewById(long id) {
		return reviewRepository.findById(id);
	}
}
