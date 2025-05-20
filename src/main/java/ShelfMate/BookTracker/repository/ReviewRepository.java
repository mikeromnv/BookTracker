package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByBook_BookId(Long bookId);
    Optional<Review> findByBook_BookIdAndUser_UserId(Long bookId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.bookId = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId")
    Integer getReviewCountByBookId(@Param("bookId") Long bookId);

    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getOverallAverageRating();

//    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r")
//    double getOverallAverageRatingSafe();

    @Query("SELECT r.book, COUNT(r) FROM Review r GROUP BY r.book ORDER BY COUNT(r) DESC LIMIT 1")
    Object[] findMostReviewedBook();

    default Book getMostReviewedBook() {
        Object[] result = findMostReviewedBook();
        if (result != null && result.length == 2) {
            Book book = (Book) result[0];
            book.setReviewCount((Long) result[1]);
            return book;
        }
        return null;
    }
}