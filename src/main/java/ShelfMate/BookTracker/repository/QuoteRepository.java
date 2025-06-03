package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Quote;
import ShelfMate.BookTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByOrderByCreatedAtDesc(); // можно сортировать по времени
    List<Quote> findAllByUser(User user);

    @Query("SELECT DISTINCT q FROM Quote q " +
            "WHERE (:quoteText IS NULL OR LOWER(q.quoteText) LIKE LOWER(CONCAT('%', :quoteText, '%'))) " +
            "AND (:bookId IS NULL OR q.book.bookId = :bookId)")
    List<Quote> findQuotesByFilters(@Param("quoteText") String quoteText,
                                  @Param("bookId") Long bookId);
}

