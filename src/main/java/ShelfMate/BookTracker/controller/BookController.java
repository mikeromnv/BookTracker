package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.dto.BookWithRatingDTO;
import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.repository.BookProgressRepository;
import ShelfMate.BookTracker.repository.BookRepository;
import ShelfMate.BookTracker.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final UserBookService userBookService;
    private final ReviewService reviewService;
    private final BookProgressService bookProgressService;

    private final BookProgressRepository bookProgressRepository;
    private final BookRepository bookRepository;

    @GetMapping
    public String showBooks(Model model, Authentication authentication,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) Long authorId,
                            @RequestParam(required = false) Long genreId) {
        //List<Book> books = bookService.getAllBooks();
        List<Book> books;
        if (title != null || authorId != null || genreId != null) {
            books = bookService.searchBooks(title, authorId, genreId);
        } else {
            books = bookService.getAllBooks();
        }
        if (authentication!= null) {
            String email = authentication.getName();
            User user = userService.getByEmail(email);
            Map<Category, List<Book>> booksByCategory = userBookService.getUserBooksGroupedByCategory(user.getUserId());

            Map<Long, String> userBookCategories = booksByCategory.entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream()
                            .map(book -> Map.entry(book.getBookId(), entry.getKey().getName())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            model.addAttribute("booksByCategory", booksByCategory);
            model.addAttribute("userBookCategories", userBookCategories);

        }
        List<BookWithRatingDTO> booksWithRatings = books.stream()
                .map(book -> {
                    Double avgRating = reviewService.getBookAverageRating(book.getBookId());
                    Integer reviewCount = reviewService.getReviewCount(book.getBookId());
                    return new BookWithRatingDTO(book, avgRating, reviewCount);
                })
                .collect(Collectors.toList());
        List<Author> authors = authorService.getAllAuthors();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("books", books);
        model.addAttribute("authors", authors);
        model.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());


        model.addAttribute("books", books);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("title", title);
        model.addAttribute("authorId", authorId);
        model.addAttribute("genreId", genreId);
        return "books";
    }




    @GetMapping("/bookform")
    public String showBookForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", genreService.getAllGenres());
        return "book/bookform"; // путь к шаблону
    }

    @PostMapping("/add")
    public String saveBook(
            @Valid @ModelAttribute("bookForm") BookForm bookForm,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            Principal principal) throws IOException {
        log.info("Получены данные: {}", bookForm);
        if (!result.hasFieldErrors("isbnNum") &&
                bookRepository.existsByIsbnNum(bookForm.getIsbnNum())) {
            result.rejectValue("isbnNum", "error.bookForm", "Книга с таким ISBN уже существует");
        }
        if (result.hasErrors()) {
            log.error("Ошибки валидации: {}", result.getAllErrors());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }


        try {
            // Сохраняем изображение
            if (!imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                Path uploadDir = Paths.get("src/main/resources/static/images/books");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                bookForm.setCoverImage(fileName);
            }
            User currentUser = userService.getByEmail(principal.getName());
            bookForm.setOwner(currentUser);



            bookService.saveBookWithAuthors(bookForm);
            return "redirect:/books?success";
        } catch (RuntimeException e) {
            log.error("Ошибка бизнес-логики: ", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }

//        return "redirect:/books";
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long bookId, Principal principal, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Книга не найдена.");
            return "redirect:/books";
        }

        User currentUser = userService.getByEmail(principal.getName());

        if (!book.getOwner().getUserId().equals(currentUser.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "Вы не можете удалить эту книгу.");
            return "redirect:/books";
        }

        bookService.deleteBook(book);
        redirectAttributes.addFlashAttribute("success", "Книга удалена.");
        return "redirect:/books";
    }

    @PostMapping("/addToCategory")
    public String addToCategory(
            @RequestParam Long bookId,
            @RequestParam String categoryName,
            Authentication authentication) {

        try {
            String userEmail = authentication.getName();
            log.info("Adding book to category for user: {}", userEmail);

            User currentUser = userService.getByEmail(userEmail);

            bookService.addBookToCategory(bookId, categoryName, currentUser.getUserId());
            return "redirect:/books?success";

        } catch (Exception e) {
            log.error("Error adding book to category", e);
            return "redirect:/books?error=" + e.getMessage();
        }
    }
    @PostMapping("/removeFromCategory")
    public String removeFromCategory(
            @RequestParam Long bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            bookService.removeBookFromCategory(bookId, user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Книга удалена из категории");
        }

        return "redirect:/books";
    }

    @PostMapping("/update-progress")
    public String updateBookProgress(@RequestParam Long bookId,
                                     @RequestParam Integer currentPage,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());

            Book book = bookService.getBookById(bookId);
            Optional<BookProgress> progressOpt = bookProgressRepository.findByUserAndBook(user, book);
            System.out.println("User: " + user.getUserId() + ", Book: " + book.getBookId());
            System.out.println("BookProgress found: " + progressOpt.isPresent());

            if (progressOpt.isPresent()) {
                BookProgress progress = progressOpt.get();
                if (currentPage < progress.getTotalPages() && currentPage >= 0) {
                    progress.setCurrentPage(currentPage);
                    progress.setUpdatedAt(LocalDateTime.now());
                    bookProgressRepository.save(progress);
                    redirectAttributes.addFlashAttribute("success", "Прогресс обновлён");
                }
                else if(currentPage.equals(progress.getTotalPages())) {
                    System.out.println("IM HERE!");
                    bookService.removeBookFromCategory(bookId, user.getUserId());
                    bookService.addBookToCategory(bookId, "Прочитано", user.getUserId());
                }
                else {
                    redirectAttributes.addFlashAttribute("error", "Неверное значение страницы");
                }
            }
        }
        return "redirect:/profile";
    }
    @PostMapping("/update-read-date")
    public String updateReadDate(@RequestParam Long bookId,
                                 @RequestParam("readDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate readDate,
                                 Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            Book book = bookService.getBookById(bookId);
            System.out.println("IM HERE\n"+user.getEmail()+"\n"+book.getBookId());
            bookService.updateReadDate(book, readDate, user);
        }
        return "redirect:/profile";

    }


    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model, Authentication authentication) {
        Book book = bookService.getBookById(id);

        if (!book.getOwner().getEmail().equals(authentication.getName())) {
            return "redirect:/access-denied";
        }

        BookForm form = bookService.convertToForm(book);
        model.addAttribute("bookForm", form);
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("authors", authorService.getAllAuthors());

        return "book/book-edit";
    }


    @PostMapping("/update")
    public String updateBook(@Valid @ModelAttribute("bookForm") BookForm form,
                             BindingResult bindingResult,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Authentication authentication,
                             Model model) throws IOException {
        Long bookId = form.getBookId();
        Book book = bookService.getBookById(bookId);
        if (bookRepository.existsByIsbnNum(form.getIsbnNum()) && !book.getIsbnNum().equals(form.getIsbnNum())) {
            bindingResult.rejectValue("isbnNum", "error.bookForm", "Книга с таким ISBN уже существует");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", genreService.getAllGenres());
            model.addAttribute("authors", authorService.getAllAuthors());
            return "book/book-edit";
        }


        User user = userService.getByEmail(authentication.getName());
        bookService.updateBook(form, imageFile, user, bookId);

        return "redirect:/books";
    }






}
