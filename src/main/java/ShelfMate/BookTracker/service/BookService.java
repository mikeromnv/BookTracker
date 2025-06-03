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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BookProgressRepository bookProgressRepository;
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
            book.setOwner(form.getOwner());

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
            form.setBookId(book.getBookId());
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
        userBook.setAddedAt(LocalDate.now());
        //System.out.println(categoryName + "\n" + (categoryName.equalsIgnoreCase("Читаю сейчас")) + "\n");

        if (cleanedCategoryName.equalsIgnoreCase("Читаю сейчас")){
            BookProgress bookProgress = new BookProgress();
            bookProgress.setBook(book);
            bookProgress.setUser(user);
            bookProgress.setCurrentPage(0);
            bookProgress.setTotalPages(book.getPageCount());
            bookProgressRepository.save(bookProgress);
        }
        if (cleanedCategoryName.equalsIgnoreCase("Прочитано") || cleanedCategoryName.equalsIgnoreCase("{Хочу прочитать}") ){
            boolean exists = bookProgressRepository.findByUserAndBook(user, book).isPresent();
            if (exists){
                removeBookFromCategory(bookId,userId);
            }
        }
        userBookRepository.save(userBook);
    }

    public Map<Long, String> getUserBookCategories(Long userId) {
        return userBookRepository.findBookCategoriesByUserId(userId).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // bookId
                        arr -> (String) arr[1] // category.name
                ));
    }


    @Transactional
    public void removeBookFromCategory(Long bookId, Long userId) {

        userBookRepository.deleteByBookBookIdAndUserId(bookId, userId);

        try{
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            BookProgress bookProgress = bookProgressRepository.findByUserAndBook(user, book)
                    .orElseThrow(() -> new RuntimeException("BookProgress not found"));
            bookProgressRepository.delete(bookProgress);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

    @Transactional
    public void updateReadDate(Book book, LocalDate readDate, User user) {
        Optional<UserBook> userBookOpt = userBookRepository.findByUserAndBook(user, book);
        if (userBookOpt.isPresent()) {
            UserBook userBook = userBookOpt.get();
            System.out.println(readDate);
            System.out.println(userBook.getAddedAt());
            userBook.setAddedAt(readDate);
            System.out.println(userBook.getAddedAt());
            userBookRepository.save(userBook);
        }
    }

    public BookForm convertToForm(Book book) {
        BookForm form = new BookForm();
        form.setBookId(book.getBookId());
        form.setTitle(book.getTitle());
        form.setIsbnNum(book.getIsbnNum());
        form.setYearPublic(book.getYearPublic());
        form.setDescription(book.getDescription());
        form.setPageCount(book.getPageCount());
        form.setCoverImage(book.getCoverUrl());
        form.setGenreId(book.getGenre().getGenreId());
        List<BookAuthor> bookAuthors = book.getBookAuthors();
        List<Long> bookAuthorsId = new ArrayList<>();
        for (BookAuthor bookAuthor : bookAuthors) {
            bookAuthorsId.add(bookAuthor.getAuthor().getAuthorId());
        }
        form.setAuthorIds(bookAuthorsId);
        return form;
    }


    @Transactional
    public void updateBook(BookForm form, MultipartFile imageFile, User user, Long bookId) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (!book.getOwner().getEmail().equals(user.getEmail())) {
            throw new SecurityException("Нет прав на редактирование");
        }

        book.setTitle(form.getTitle());
        book.setIsbnNum(form.getIsbnNum());
        book.setYearPublic(form.getYearPublic());
        book.setDescription(form.getDescription());
        book.setPageCount(form.getPageCount());

        Genre genre = genreRepository.findById(form.getGenreId()).orElse(null);
        book.setGenre(genre);


        book.getBookAuthors().clear();
        for (Long authorId : form.getAuthorIds()) {
            Author author = authorRepository.findById(authorId).orElse(null);
            BookAuthor bookAuthor = new BookAuthor();
            BookAuthorId bookAuthorId = new BookAuthorId(book.getBookId(), authorId);
            bookAuthor.setId(bookAuthorId);
            bookAuthor.setAuthor(author);
            bookAuthor.setBook(book);

            book.getBookAuthors().add(bookAuthor);
        }
        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            Path uploadDir = Paths.get("src/main/resources/static/images/books");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("IM HERE!");
            System.out.println(fileName);
            book.setCoverUrl(fileName);
        }

        bookRepository.save(book);
    }

    public List<Book> searchBooks(String title, Long authorId, Long genreId) {
        return bookRepository.findBooksByFilters(title, authorId, genreId);
    }


}
