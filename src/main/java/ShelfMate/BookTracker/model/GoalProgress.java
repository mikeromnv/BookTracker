package ShelfMate.BookTracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgress {
    private Integer goal;
    private Integer readCount;
    private Integer percentage;
    private boolean hasGoal;
}