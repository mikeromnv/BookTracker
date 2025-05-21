package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findAllByOrderByCreatedAtDesc(); // можно сортировать по времени
}

