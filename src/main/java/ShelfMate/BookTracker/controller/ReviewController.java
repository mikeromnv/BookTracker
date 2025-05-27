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

    @GetMapping("/add-review")
    public String showAddReviewForm(Model model) {

        List<Book> books = bookService.getAllBooks(); // или bookRepository.findAll()
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
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getByEmail(authentication.getName());
            Book book = bookService.getBookById(review.getBook().getBookId());

            review.setUser(user);
            review.setBook(book);
            review.setCreatedAt(LocalDateTime.now());
            review.setIsNew(false);

            reviewRepository.save(review);

            redirectAttributes.addFlashAttribute("success", "Отзыв успешно сохранен!");
            return "redirect:/allreviews";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении отзыва");
            return "redirect:/allreviews/add-review";
        }
    }

    // Показать форму редактирования
    @GetMapping("/edit/{id}")
    public String showEditReviewForm(@PathVariable Long id, Model model) {
        Review review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        model.addAttribute("books", bookService.getAllBooks()); // нужно, если книга участвует в select
        return "add-review"; // имя существующего шаблона
    }


    // Обработка формы редактирования
    @PostMapping("/edit/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute("review") Review updatedReview) {
        reviewService.updateReview(id, updatedReview);
        return "redirect:/allreviews";
    }

    // Удаление отзыва
    @DeleteMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, Principal principal) {
        Review review = reviewService.getReviewById(id);
        if (review != null && review.getUser().getEmail().equals(principal.getName())) {
            reviewService.deleteReview(id);
        }
        return "redirect:/allreviews";
    }



}