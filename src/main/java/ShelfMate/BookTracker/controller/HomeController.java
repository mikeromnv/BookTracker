package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserService userService;

    public HomeController(BookService bookService,
                          AuthorService authorService,
                          UserService userService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.getByEmail(userDetails.getUsername());
            model.addAttribute("username", user.getUsername());
        }

        int count = 3;
        model.addAttribute("random_books", bookService.getRandomBooks(count));
        model.addAttribute("random_authors", authorService.getRandomAuthors(count));

        return "index";
    }
}