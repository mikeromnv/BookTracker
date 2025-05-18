package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.GoalService;
import ShelfMate.BookTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    @GetMapping("/index")
    public String showIndex(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            GoalProgress progress = goalService.getReadingGoalProgress(user.getUserId());
            model.addAttribute("goalProgress", progress);
        }
        return "index";
    }


}