package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;


    public GoalProgress getReadingGoalProgress(Long userId) {
        return goalRepository.findReadingGoalProgress(userId)
                .orElseGet(() -> {
                    int readCount = goalRepository.countReadBooks(userId);
                    return new GoalProgress(0, readCount, 0, false);
                });
    }


}
