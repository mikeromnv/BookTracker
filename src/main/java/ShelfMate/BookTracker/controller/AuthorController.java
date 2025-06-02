package ShelfMate.BookTracker.controller;


import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/authors")
@Data
public class AuthorController {

    @Autowired
    private AuthorRepository authorRepository;
    private final AuthorService authorService;
    private final UserService userService;

    @GetMapping("/new")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author/author_form";
    }

    @PostMapping("/add")
    public String saveAuthor(@ModelAttribute("author") Author author, Authentication authentication) {
        try {
            User user = userService.getByEmail(authentication.getName());
            author.setOwner(user);
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


    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long authorId, Principal principal, RedirectAttributes redirectAttributes) {
        Author author = authorService.getAuthorById(authorId);
        if (author == null) {
            redirectAttributes.addFlashAttribute("error", "Автор не найден.");
            return "redirect:/authors";
        }

        User currentUser = userService.getByEmail(principal.getName());

        if (!author.getOwner().getUserId().equals(currentUser.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "Вы не можете удалить этого автора.");
            return "redirect:/authors";
        }

        authorService.deleteAuthor(author);
        redirectAttributes.addFlashAttribute("success", "Автор удален.");
        return "redirect:/authors";
    }
}
