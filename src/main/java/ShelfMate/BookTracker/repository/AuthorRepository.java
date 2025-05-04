package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAll();

    @Query(value = "SELECT * FROM author ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Author> findRandomAuthors(@Param("limit") int limit);

}
