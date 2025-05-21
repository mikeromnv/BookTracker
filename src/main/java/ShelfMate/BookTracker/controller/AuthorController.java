package ShelfMate.BookTracker.controller;


import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.service.AuthorService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/authors")
@Data
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;
    private final AuthorService authorService;

    @GetMapping("/new")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author/author_form"; // путь к шаблону
    }

    @PostMapping("/add")
    public String saveAuthor(@ModelAttribute("author") Author author) {
        try {
            authorRepository.save(author);
            return "redirect:/books/bookform?authorSuccess";
        } catch (Exception e) {
            return "redirect:/authors/new?error=" + e.getMessage();
        }
    }
    @GetMapping
    public String showAllAuthors(Model model) {
        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "allauthors";
    }

}
