package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserGoal;
import ShelfMate.BookTracker.service.GoalService;
import ShelfMate.BookTracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserService userService;
    private final GoalService goalService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showSettings(Authentication authentication, Model model) {
        User user = userService.getByEmail(authentication.getName());
        UserGoal readingGoal = goalService.getUserReadingGoal(user);

        model.addAttribute("user", user);
        model.addAttribute("goal", readingGoal != null ? readingGoal : new UserGoal());
        return "settings";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "settings";
        }

        User currentUser = userService.getByEmail(authentication.getName());
        currentUser.setUsername(user.getUsername());
        currentUser.setEmail(user.getEmail());

        userService.updateUser(currentUser);
        redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");
        return "redirect:/settings";
    }

    @PostMapping("/goal")
    public String updateReadingGoal(
            @RequestParam Integer targetValue,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadline,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userService.getByEmail(authentication.getName());
        goalService.updateReadingGoal(user, targetValue, deadline);

        redirectAttributes.addFlashAttribute("success", "Цель чтения обновлена");
        return "redirect:/settings";
    }
}
