package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.BookAuthor;
import ShelfMate.BookTracker.model.BookAuthorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {
}
