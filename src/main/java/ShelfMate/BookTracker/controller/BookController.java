package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

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
        model.addAttribute("bookForm", new BookForm()); // Убедитесь, что имя совпадает с th:object в форме
        model.addAttribute("genres", genreService.getAllGenres());   // Если используется select
        return "book/bookform";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("bookForm") @Valid BookForm bookForm,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }

        try {
            bookService.addNewBook(bookForm);
            return "redirect:/books?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении книги: " + e.getMessage());
            model.addAttribute("books", bookService.getAllBooks());
            model.addAttribute("genres", genreService.getAllGenres());
            return "book/bookform";
        }
    }



}
