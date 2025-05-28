package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.service.BookProgressService;
import ShelfMate.BookTracker.service.UserBookService;
import ShelfMate.BookTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        List<UserBook> userBooks = userBookService.getByUser(user);
        Map<Book, LocalDate> readDateMap = new HashMap<>();
        for (UserBook ub : userBooks) {
            if (ub.getAddedAt() != null) {
                readDateMap.put(ub.getBook(), ub.getAddedAt());
            }
        }


        model.addAttribute("user", user);
        model.addAttribute("booksByCategory", booksByCategory);
        model.addAttribute("bookProgress", bookProgresses);
        model.addAttribute("currentPageMap", currentPageMap);
        model.addAttribute("readDateMap", readDateMap);
        return "profile";
    }
}
