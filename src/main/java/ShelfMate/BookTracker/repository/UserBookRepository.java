package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    List<UserBook> findByUserUserId(Long userId);
    @Query("SELECT ub.book.bookId, ub.category.name FROM UserBook ub WHERE ub.user.userId = :userId")
    List<Object[]> findBookCategoriesByUserId(@Param("userId") Long userId);


    @Query("SELECT ub FROM UserBook ub JOIN FETCH ub.book JOIN FETCH ub.category WHERE ub.user.userId = :userId")
    List<UserBook> findByUserIdWithCategory(@Param("userId") Long userId);

    @Query("DELETE FROM UserBook ub WHERE ub.book.bookId = :bookId AND ub.user.userId = :userId")
    @Modifying
    void deleteByBookBookIdAndUserId(Long bookId, Long userId);

    Optional<UserBook> findByUserAndBook(User user, Book book);
    List<UserBook> findByUser(User user);

    @Query("SELECT ub FROM UserBook ub WHERE ub.user.userId = :userId AND ub.addedAt >= :fromDate AND ub.category.name='Прочитано' ")
    List<UserBook> findReadBooksInLastYear(@Param("userId") Long userId, @Param("fromDate") LocalDate fromDate);


}
