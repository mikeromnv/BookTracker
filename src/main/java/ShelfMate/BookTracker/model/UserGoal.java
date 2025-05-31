package ShelfMate.BookTracker.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usergoal")
@Data
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "goal_type_id", nullable = false)
    private GoalType goalType;

    @Column(name = "target_value", nullable = false)
    @Positive
    private Integer targetValue;

    @Column(name = "progress_value", nullable = false)
    private Integer progressValue = 0;

    @Column(name = "start_date", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate startDate = LocalDate.now();

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (deadline.isBefore(startDate)) {
            throw new IllegalArgumentException("Deadline must be after start date");
        }
    }
}