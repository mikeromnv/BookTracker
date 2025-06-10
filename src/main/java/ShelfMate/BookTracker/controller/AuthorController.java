package ShelfMate.BookTracker.controller;


import ShelfMate.BookTracker.dto.AuthorForm;
import ShelfMate.BookTracker.dto.BookForm;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    public String showAllAuthors(Model model, @RequestParam(required = false) String authorName) {
        List<Author> authors;
        if (authorName != null && !authorName.trim().isEmpty()) {
            authors = authorService.searchAuthors(authorName);
        } else {
            authors = authorService.getAllAuthors();
        }

        model.addAttribute("authors", authors);
        model.addAttribute("authorName", authorName);
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

    @GetMapping("/edit/{id}")
    public String editAuthorForm(@PathVariable Long id, Model model, Authentication authentication) {

        Author author = authorService.getAuthorById(id);
        if (!author.getOwner().getEmail().equals(authentication.getName())) {
            return "redirect:/access-denied";
        }

        AuthorForm form = authorService.convertToForm(author); // Преобразование сущности в форму
        model.addAttribute("authorForm", form);
        return "author/author-edit"; // Имя шаблона редактирования
    }


    @PostMapping("/update")
    public String updateBook(@ModelAttribute("authorForm") AuthorForm form,
                             Authentication authentication) throws IOException {
        Long authorId = form.getAuthorId();
        User user = userService.getByEmail(authentication.getName());
        authorService.updateAuthor(form, user, authorId);

        return "redirect:/authors";
    }
    @GetMapping("/search")
    @ResponseBody
    public List<AuthorForm> searchAuthors(@RequestParam String name) {
        return authorService.findByNameContainingIgnoreCase(name)
                .stream()
                .map(author -> new AuthorForm(author.getAuthorId(), author.getAuthorName(), author.getBio()))
                .collect(Collectors.toList());
    }



}
