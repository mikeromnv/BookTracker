package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Review;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.ReviewService;
import ShelfMate.BookTracker.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/allreviews")
@RequiredArgsConstructor

public class ReviewController {
    private final ReviewService reviewService;
    private final BookService bookService;
    private final UserService userService;


    @GetMapping
    public String getAllReviews(
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Получаем отфильтрованные отзывы
        Page<Review> reviewsPage = reviewService.getAllReviews(bookId, userId, minRating, page-1, size);

        // Получаем дополнительные данные для фильтров
        List<Book> books = bookService.getAllBooks();
        List<User> users = userService.getAllUsers();

        // Статистика
        Long totalReviews = reviewService.getTotalReviewsCount();
        Double averageRating = reviewService.getOverallAverageRating();
        Book mostReviewedBook = reviewService.getMostReviewedBook();

        model.addAttribute("reviews", reviewsPage.getContent());
        model.addAttribute("books", books);
        model.addAttribute("users", users);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("mostReviewedBook", mostReviewedBook);

        // Пагинация
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewsPage.getTotalPages());

        return "allreviews";
    }
}