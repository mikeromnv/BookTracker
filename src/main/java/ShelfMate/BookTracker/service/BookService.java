package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.BookAuthor;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.repository.BookRepository;
import ShelfMate.BookTracker.repository.GenreRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final FileStorageService fileStorageService;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Book> getRandomBooks(int count) {
        return bookRepository.findRandomBooks(count);
    }

    @Transactional
    public void addNewBook(BookForm bookForm) throws IOException {
        // Сохранение обложки
        String coverUrl = null;
        if (bookForm.getCoverImage() != null && !bookForm.getCoverImage().isEmpty()) {
            coverUrl = fileStorageService.storeFile(bookForm.getCoverImage());
        }

        // Получение или создание авторов
        Set<Author> authors = Arrays.stream(bookForm.getAuthors().split(","))
                .map(String::trim)
                .map(name -> authorRepository.findByAuthorName(name)
                        .orElseGet(() -> {
                            Author newAuthor = new Author();
                            newAuthor.setAuthorName(name);
                            return authorRepository.save(newAuthor);
                        }))
                .collect(Collectors.toSet());
        List<BookAuthor> authors1 = new ArrayList<>();

        // Создание книги
        Book book = new Book();
        book.setTitle(bookForm.getTitle());
        book.setGenre(genreRepository.findById(bookForm.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("Жанр не найден")));
        book.setIsbnNum(bookForm.getIsbn());
        book.setYearPublic(bookForm.getYear());
        book.setPageCount(bookForm.getPageCount());
        book.setDescription(bookForm.getDescription());
        book.setCoverUrl(coverUrl);
        for (Author author : authors) {
            BookAuthor authorBook = new BookAuthor();
            authorBook.setAuthor(author);
            authorBook.setBook(book);
            authors1.add(authorBook);
        }
        book.setBookAuthors(authors1);

        bookRepository.save(book);
    }
}
