package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserBook;
import ShelfMate.BookTracker.repository.UserBookRepository;
import ShelfMate.BookTracker.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Data
@AllArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserService userService;
    private final GoalService goalService;
    private final UserBookRepository userBookRepository;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.getByEmail(userDetails.getUsername());
            model.addAttribute("username", user.getUsername());

            GoalProgress goalProgress = goalService.getCurrentReadingGoalProgress(user.getUserId());
            model.addAttribute("goalProgress", goalProgress);

            LocalDate now = LocalDate.now();
            LocalDate fromDate = now.minusMonths(11).withDayOfMonth(1);
            List<UserBook> books = userBookRepository.findReadBooksInLastYear(user.getUserId(), fromDate);

            Map<YearMonth, Long> countPerMonth = books.stream()
                    .collect(Collectors.groupingBy(
                            book -> YearMonth.from(book.getAddedAt()),
                            TreeMap::new,
                            Collectors.counting()
                    ));

            Map<String, Long> completeData = new LinkedHashMap<>();
            for (int i = 11; i >= 0; i--) {
                YearMonth ym = YearMonth.now().minusMonths(i);
                String label = ym.getMonth().getDisplayName(TextStyle.FULL, new Locale("ru")) + " " + ym.getYear();
                completeData.put(label, countPerMonth.getOrDefault(ym, 0L));
            }

            model.addAttribute("monthLabels", new ArrayList<>(completeData.keySet()));
            model.addAttribute("activityData", new ArrayList<>(completeData.values()));
        }

        int count = 4;
        model.addAttribute("random_books", bookService.getRandomBooks(count));
        model.addAttribute("random_authors", authorService.getRandomAuthors(count));

        return "index";
    }
}