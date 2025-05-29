package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Quote;
import ShelfMate.BookTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByOrderByCreatedAtDesc(); // можно сортировать по времени
    List<Quote> findAllByUser(User user);
}

