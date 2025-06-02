package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    private final BookService bookService;
    private final CategoryService categoryService;

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

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        model.addAttribute("booksByCategory", booksByCategory);
        model.addAttribute("bookProgress", bookProgresses);
        model.addAttribute("currentPageMap", currentPageMap);
        model.addAttribute("readDateMap", readDateMap);
        return "profile";
    }


    @PostMapping("/addToCategory")
    public String addToCategory(
            @RequestParam Long bookId,
            @RequestParam String categoryName,
            Authentication authentication) {

        try {
            String userEmail = authentication.getName();
            //log.info("Adding book to category for user: {}", userEmail);

            User currentUser = userService.getByEmail(userEmail);

            bookService.addBookToCategory(bookId, categoryName, currentUser.getUserId());
            return "redirect:/profile?success";

        } catch (Exception e) {
            //log.error("Error adding book to category", e);
            return "redirect:/profile?error=" + e.getMessage();
        }
    }
    @PostMapping("/removeFromCategory")
    public String removeFromCategory(
            @RequestParam Long bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            bookService.removeBookFromCategory(bookId, user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Книга удалена из категории");
        }

        return "redirect:/profile";
    }

}
