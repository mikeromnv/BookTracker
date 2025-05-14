package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Category;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private final CategoryService categoryService;

    @GetMapping
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        List<Author> authors = authorService.getAllAuthors();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
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
            @RequestParam("imageFile") MultipartFile imageFile,
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







}
