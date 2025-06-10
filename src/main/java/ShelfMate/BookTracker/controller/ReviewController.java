package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Review;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.repository.ReviewRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/allreviews")
@RequiredArgsConstructor

public class ReviewController {
    private final ReviewService reviewService;
    private final BookService bookService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;

    @GetMapping
    public String getAllReviews(
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // отфильтрованные отзывы
        Page<Review> reviewsPage = reviewService.getAllReviews(bookId, userId, minRating, page-1, size);

        //  данные для фильтров
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

    @GetMapping("/add-review")
    public String showAddReviewForm(Model model) {

        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        Review review = new Review();
        review.setRating(1);
        review.setReviewText("");
        review.setBook(books.get(0));
        review.setIsNew(true);
        model.addAttribute("review", review);
        return "add-review";
    }

    @PostMapping("/save-review")
    public String saveReview(@ModelAttribute("review") Review review,
                             BindingResult bindingResult,
                             Model model,
                             Authentication authentication) {

        User user = userService.getByEmail(authentication.getName());
        Book book = bookService.getBookById(review.getBook().getBookId());

        if (reviewRepository.existsByUserAndBook(user, book)) {
            bindingResult.rejectValue("book.bookId", "review.exists", "Вы уже оставляли отзыв на эту книгу");
        }
        if (bindingResult.hasErrors()) {
            review.setIsNew(true);
            model.addAttribute("books", bookService.getAllBooks());
            return "/add-review";
        }

        review.setUser(user);
        review.setBook(book);
        review.setCreatedAt(LocalDateTime.now());
        review.setIsNew(false);

        reviewRepository.save(review);

        return "redirect:/allreviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model) {
        Review review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        model.addAttribute("books", bookService.getAllBooks());
        return "add-review";
    }

    @PostMapping("/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute("review") Review updatedReview) {
        reviewService.updateReview(id, updatedReview);
        return "redirect:/allreviews";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Principal principal) {
        Review review = reviewService.getReviewById(id);
        if (review != null && review.getUser().getEmail().equals(principal.getName())) {
            reviewService.deleteReview(id);
        }
        return "redirect:/allreviews";
    }



}