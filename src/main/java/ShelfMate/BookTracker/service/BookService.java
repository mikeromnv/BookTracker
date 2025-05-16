package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.repository.*;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;
    private final BookAuthorRepository bookAuthorRepository;
    private final CategoryService categoryRepository;
    private final UserBookRepository userBookRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Book> getRandomBooks(int count) {
        return bookRepository.findRandomBooks(count);
    }
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
        public void saveBookWithAuthors(BookForm form) throws IOException {
            Book book = new Book();
            book.setTitle(form.getTitle());
            book.setIsbnNum(form.getIsbnNum());
            book.setYearPublic(form.getYearPublic());
            book.setDescription(form.getDescription());
            book.setPageCount(form.getPageCount());
            book.setCoverUrl(form.getCoverImage());


            if (form.getGenreId() != null) {
                Genre genre = genreRepository.findById(form.getGenreId())
                        .orElseThrow(() -> new RuntimeException("Жанр не найден"));
                book.setGenre(genre);
            }

            Book savedBook = bookRepository.save(book);

            if (form.getAuthorIds() != null && !form.getAuthorIds().isEmpty()) {
                for (Long authorId : form.getAuthorIds()) {
                    Author author = authorRepository.findById(authorId)
                            .orElseThrow(() -> new RuntimeException("Автор не найден"));

                    BookAuthor bookAuthor = new BookAuthor();
                    BookAuthorId bookAuthorId = new BookAuthorId(savedBook.getBookId(), author.getAuthorId());
                    bookAuthor.setId(bookAuthorId);
                    bookAuthor.setBook(savedBook);
                    bookAuthor.setAuthor(author);

                    bookAuthorRepository.save(bookAuthor);
                }
            }
    }
    @Transactional
    public void addBookToCategory(Long bookId, String categoryName, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String cleanedCategoryName = categoryName.trim().replaceAll("^[,\\s]+", "");
        Category category = categoryRepository.findByName(cleanedCategoryName);

        UserBook userBook = new UserBook();
        UserBookId userBookId = new UserBookId(userId, bookId); // составной ключ
        userBook.setId(userBookId);
        userBook.setBook(book);
        userBook.setUser(user);
        userBook.setCategory(category);

        userBookRepository.save(userBook);
    }

    public Map<Long, String> getUserBookCategories(Long userId) {
        return userBookRepository.findBookCategoriesByUserId(userId).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // bookId
                        arr -> (String) arr[1] // category.name
                ));
    }

}
