package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findAll();

    @Query(value = "SELECT * FROM book ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooks(@Param("limit") int limit);

    Book findById(long id);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.bookAuthors ba " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:authorId IS NULL OR ba.author.authorId = :authorId) " +
            "AND (:genreId IS NULL OR b.genre.genreId = :genreId)")
    List<Book> findBooksByFilters(@Param("title") String title,
                                  @Param("authorId") Long authorId,
                                  @Param("genreId") Long genreId);

}