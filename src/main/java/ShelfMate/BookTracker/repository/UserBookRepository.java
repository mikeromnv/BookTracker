package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {

}
