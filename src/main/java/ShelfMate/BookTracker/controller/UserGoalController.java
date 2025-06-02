package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.model.GoalType;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserGoal;
import ShelfMate.BookTracker.repository.GoalTypeRepository;
import ShelfMate.BookTracker.repository.UserGoalRepository;
import ShelfMate.BookTracker.service.UserGoalService;
import ShelfMate.BookTracker.service.UserService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user-goals")
@Data
public class UserGoalController {
    private final UserService userService;
    private final UserGoalService userGoalService;
    private final UserGoalRepository userGoalRepository;
    private final GoalTypeRepository goalTypeRepository;

    @GetMapping
    public String viewGoalsPage(Model model, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        List<UserGoal> goals = userGoalService.getAllGoals(user);
        boolean hasActiveGoal = userGoalService.hasActiveYearlyGoal(user);
        if (hasActiveGoal) {
            UserGoal activeGoal = userGoalService.getActiveGoal(user);
            model.addAttribute("activeGoal", activeGoal);
        }
        model.addAttribute("goals", goals);
        model.addAttribute("hasActiveGoal", hasActiveGoal);
        return "user-goals";
    }
    @GetMapping("/yearly-goals")
    public String yearlyGoalsPage(Model model, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());

        // Все годовые цели пользователя
        List<UserGoal> yearlyGoals = userGoalRepository.findAllByUserAndGoalTypeNameOrderByStartDateDesc(user, "Годовая цель");

        // Проверка на наличие активной цели
        boolean hasActiveGoal = yearlyGoals.stream()
                .anyMatch(goal -> goal.getDeadline().isAfter(LocalDate.now()) && goal.getProgressValue() < goal.getTargetValue());

        model.addAttribute("yearlyGoals", yearlyGoals);
        model.addAttribute("hasActiveGoal", hasActiveGoal);
        model.addAttribute("newGoal", new UserGoal()); // для формы

        return "user-goals/yearly-goals";
    }
    @PostMapping("/yearly-goals")
    public String addYearlyGoal(@ModelAttribute("newGoal") UserGoal newGoal,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        User user = userService.getByEmail(authentication.getName());

        boolean alreadyExists = userGoalRepository.existsByUserAndGoalTypeNameAndDeadlineAfter(
                user, "Годовая цель", LocalDate.now());

        if (alreadyExists) {
            redirectAttributes.addFlashAttribute("error", "У вас уже есть активная годовая цель.");
            return "redirect:/user-goals/yearly-goals";
        }

        GoalType yearlyType = goalTypeRepository.findByName("Годовая цель")
                .orElseThrow(IllegalStateException::new);

        newGoal.setUser(user);
        newGoal.setGoalType(yearlyType);
        newGoal.setStartDate(LocalDate.now());
        newGoal.setCreatedAt(LocalDateTime.now());

        userGoalRepository.save(newGoal);

        redirectAttributes.addFlashAttribute("success", "Годовая цель добавлена!");
        return "redirect:/user-goals";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteGoal(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.getByEmail(authentication.getName());

        Optional<UserGoal> goalOpt = userGoalRepository.findById(id);
        if (goalOpt.isPresent()) {
            UserGoal goal = goalOpt.get();
            if (goal.getUser().equals(user)) {
                userGoalRepository.delete(goal);
                redirectAttributes.addFlashAttribute("success", "Цель удалена.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Вы не можете удалить чужую цель.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Цель не найдена.");
        }

        return "redirect:/user-goals";
    }


}
