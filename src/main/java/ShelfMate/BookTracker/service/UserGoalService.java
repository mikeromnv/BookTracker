package ShelfMate.BookTracker.service;
import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.repository.GoalTypeRepository;
import ShelfMate.BookTracker.repository.UserGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final GoalTypeRepository goalTypeRepository;

    @Autowired
    public UserGoalService(UserGoalRepository userGoalRepository, GoalTypeRepository goalTypeRepository) {
        this.userGoalRepository = userGoalRepository;
        this.goalTypeRepository = goalTypeRepository;
    }

    public List<UserGoal> getYearlyGoals(User user) {
        return userGoalRepository.findAllByUserAndGoalTypeNameOrderByStartDateDesc(user, "Годовая цель");
    }

    public boolean hasActiveYearlyGoal(User user) {
        return userGoalRepository.existsByUserAndGoalTypeNameAndDeadlineAfter(user, "Годовая цель", LocalDate.now());
    }
    public UserGoal getActiveGoal(User user) {
        return userGoalRepository.findByUserAndGoalTypeNameAndDeadlineAfter(user, "Годовая цель", LocalDate.now());
    }

    public void addYearlyGoal(User user, int targetValue, LocalDate deadline) {
        if (hasActiveYearlyGoal(user)) {
            throw new IllegalStateException("У вас уже есть активная годовая цель.");
        }

        GoalType goalType = goalTypeRepository.findByName("Годовая цель")
                .orElseThrow(IllegalStateException::new);
        if (goalType == null) {
            throw new IllegalStateException("Тип цели 'Годовая цель' не найден.");
        }

        UserGoal goal = new UserGoal();
        goal.setUser(user);
        goal.setGoalType(goalType);
        goal.setTargetValue(targetValue);
        goal.setProgressValue(0);
        goal.setStartDate(LocalDate.now());
        goal.setDeadline(deadline);
        goal.setCreatedAt(LocalDateTime.now());

        userGoalRepository.save(goal);
    }

    public List<UserGoal> getAllGoals(User user) {
        return userGoalRepository.findAllByUser(user);
    }
}

