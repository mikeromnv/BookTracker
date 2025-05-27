package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.BookProgress;
import ShelfMate.BookTracker.model.Category;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.BookProgressService;
import ShelfMate.BookTracker.service.UserBookService;
import ShelfMate.BookTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserBookService userBookService;
    private final UserService userService;
    private final BookProgressService bookProgressService;
    @GetMapping
    public String getUserProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getByEmail(email);

        Map<Category, List<Book>> booksByCategory = userBookService.getUserBooksGroupedByCategory(user.getUserId());

        List<BookProgress> bookProgresses = bookProgressService.getByUser(user);

        Map<Book, Integer> currentPageMap = new HashMap<>();
        for (BookProgress bp : bookProgresses) {
            currentPageMap.put(bp.getBook(), bp.getCurrentPage());
        }

        model.addAttribute("user", user);
        model.addAttribute("booksByCategory", booksByCategory);
        model.addAttribute("bookProgress", bookProgresses);
        model.addAttribute("currentPageMap", currentPageMap);
        return "profile";
    }
}
