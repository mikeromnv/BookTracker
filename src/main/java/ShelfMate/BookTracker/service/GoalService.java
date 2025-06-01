package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.GoalType;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserGoal;
import ShelfMate.BookTracker.repository.GoalRepository;
import ShelfMate.BookTracker.repository.GoalTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalTypeRepository goalTypeRepository;

    public GoalProgress getCurrentReadingGoalProgress(Long userId) {
        return goalRepository.findReadingGoalProgress(userId)
                .orElseGet(() -> {
                    int readCount = goalRepository.countReadBooks(userId);
                    return new GoalProgress(0, readCount, 0, false);
                });
    }

    public UserGoal getUserReadingGoal(User user) {
        return goalRepository.findByUserAndGoalTypeName(user, "Годовая цель")
                .orElse(null);
    }

    public void updateReadingGoal(User user, Integer targetValue, LocalDate deadline) {
        GoalType readingGoalType = goalTypeRepository.findByName("Годовая цель")
                .orElseThrow(() -> new RuntimeException("Тип цели не найден"));

        UserGoal goal = goalRepository.findActualGoalByUserAndGoalType(user, readingGoalType.getTypeId())
                .orElse(new UserGoal());

        goal.setUser(user);
        goal.setGoalType(readingGoalType);
        goal.setTargetValue(targetValue);
        goal.setDeadline(deadline);

        goalRepository.save(goal);
    }
}
