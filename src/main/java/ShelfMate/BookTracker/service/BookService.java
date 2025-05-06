package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.BookAuthor;
import ShelfMate.BookTracker.model.Genre;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.repository.BookAuthorRepository;
import ShelfMate.BookTracker.repository.BookRepository;
import ShelfMate.BookTracker.repository.GenreRepository;
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

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Book> getRandomBooks(int count) {
        return bookRepository.findRandomBooks(count);
    }



    @Transactional
    public void saveBookWithAuthors(BookForm form) throws IOException {
        Book book = new Book();
        book.setTitle(form.getTitle());
        book.setIsbnNum(form.getIsbnNum());
        book.setYearPublic(form.getYearPublic());
        book.setDescription(form.getDescription());
        book.setPageCount(form.getPageCount());

        String coverUrl = null;
        if (form.getCoverImage() != null && !form.getCoverImage().isEmpty()) {
            coverUrl = fileStorageService.storeFile(form.getCoverImage());
        }

        book.setCoverUrl(coverUrl);

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

                // Создаем и сохраняем связь
                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setBook(savedBook);
                bookAuthor.setAuthor(author);
                bookAuthorRepository.save(bookAuthor);
            }
        }



    }

}
