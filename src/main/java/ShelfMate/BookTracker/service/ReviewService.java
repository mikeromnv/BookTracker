package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Review;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.repository.ReviewRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;

    public Review createOrUpdateReview(Long bookId, String userEmail, Integer rating, String reviewText) {
        Book book = bookService.getBookById(bookId);
        User user = userService.getByEmail(userEmail);

        Optional<Review> existingReview = reviewRepository.findByBook_BookIdAndUser_UserId(bookId, user.getUserId());
        Review review = existingReview.orElse(new Review());

        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewText(reviewText);

        return reviewRepository.save(review);
    }

    public List<Review> getBookReviews(Long bookId) {
        return reviewRepository.findByBook_BookId(bookId);
    }

    public Double getBookAverageRating(Long bookId) {
        Double avg = reviewRepository.getAverageRatingByBookId(bookId);
        return avg != null ? Math.round(avg * 10) / 10.0 : null;
    }

    public Integer getReviewCount(Long bookId) {
        return reviewRepository.getReviewCountByBookId(bookId);
    }


    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void updateReview(Long id, Review updatedReview) {
        Review existing = getReviewById(id);
        existing.setRating(updatedReview.getRating());
        existing.setReviewText(updatedReview.getReviewText());
        reviewRepository.save(existing);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }


    public Page<Review> getAllReviews(Long bookId, Long userId, Integer minRating, int page, int size) {
        Specification<Review> spec = Specification.where(null);

        if (bookId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("book").get("bookId"), bookId));
        }

        if (userId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("userId"), userId));
        }

        if (minRating != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("rating"), minRating));
        }

        return reviewRepository.findAll(spec, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Long getTotalReviewsCount() {
        return reviewRepository.count();
    }

    public Double getOverallAverageRating() {
        return reviewRepository.getOverallAverageRating();
    }

    public Book getMostReviewedBook() {
        return reviewRepository.getMostReviewedBook();
    }
}
