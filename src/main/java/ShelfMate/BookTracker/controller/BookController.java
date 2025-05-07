package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.GenreService;
import ShelfMate.BookTracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final UserService userService;

    @GetMapping
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("books", books);
        model.addAttribute("authors", authors);
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
            @ModelAttribute("bookForm") @Valid BookForm bookForm,
            BindingResult result,
            Model model) throws IOException {
        log.info("Получены данные: {}", bookForm);
        if (result.hasErrors()) {
            log.error("Ошибки валидации: {}", result.getAllErrors());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }

        try {
            bookService.saveBookWithAuthors(bookForm);
            return "redirect:/books?success";
        } catch (Exception e) {
            log.error("Ошибка сохранения: ", e);
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }
//        return "redirect:/books";
    }
    @GetMapping("/books/add-to-category")
    public String addToCategory(@RequestParam Long bookId, @RequestParam String categoryName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // Get logged in username

        User currentUser = userService.findUserByUsername(currentUserName);
        if(currentUser == null){
            throw new RuntimeException("User not found");
        }

        bookService.addBookToCategory(bookId, categoryName, currentUser.getUserId()); // Pass userId

        return "redirect:/books"; // Redirect back to the books page
    }







}
