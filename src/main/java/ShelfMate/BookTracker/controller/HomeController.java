package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.GoalService;
import ShelfMate.BookTracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Data
@AllArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserService userService;
    private final GoalService goalService;


    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.getByEmail(userDetails.getUsername());
            model.addAttribute("username", user.getUsername());

            GoalProgress goalProgress = goalService.getReadingGoalProgress(user.getUserId());
            model.addAttribute("goalProgress", goalProgress);

        }

        int count = 3;
        model.addAttribute("random_books", bookService.getRandomBooks(count));
        model.addAttribute("random_authors", authorService.getRandomAuthors(count));

        return "index";
    }
}