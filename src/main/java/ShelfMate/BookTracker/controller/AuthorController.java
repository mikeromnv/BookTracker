package ShelfMate.BookTracker.controller;


import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping("/new")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author/author_form"; // путь к шаблону
    }

    @PostMapping("/add")
    public String saveAuthor(@ModelAttribute("author") Author author) {
        authorRepository.save(author);
        return "redirect:/books/new"; // возвращаемся к форме книги
    }

}
