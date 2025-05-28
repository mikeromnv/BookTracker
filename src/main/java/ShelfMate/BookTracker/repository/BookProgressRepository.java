package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookProgressRepository extends JpaRepository<BookProgress, BookProgressId> {

    Optional<BookProgress> findByUserAndBook(User user, Book book);
    List<BookProgress> findByUser(User user);
}
