package es.codeurjc.practica1.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.practica1.dto.ReviewDTO;
import es.codeurjc.practica1.dto.ReviewsMapper;
import es.codeurjc.practica1.model.Review;
import es.codeurjc.practica1.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewsMapper reviewsMapper;

    @GetMapping("/")
    public List<ReviewDTO> getAllReviews() {
        return reviewService.findAll()
                .stream()
                .map(reviewsMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable long id) {
        Optional<Review> reviewOpt = reviewService.findById(id);
        return reviewOpt.map(review -> ResponseEntity.ok(reviewsMapper.toDTO(review)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        Review review = reviewsMapper.toDomain(reviewDTO);
        reviewService.save(review);
        ReviewDTO savedDTO = reviewsMapper.toDTO(review);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(savedDTO);
    }

    @DeleteMapping("/{id}")
    public ReviewDTO deleteReview(@PathVariable long id) {
        Optional<Review> reviewOpt = reviewService.findById(id);
        if (reviewOpt.isPresent()) {
            reviewService.delete(reviewOpt.get());
            return reviewsMapper.toDTO(reviewOpt.get());
        } else {
            return null;
        }
    }
}
