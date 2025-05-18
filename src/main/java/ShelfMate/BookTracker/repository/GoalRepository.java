package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<UserGoal, Long> {

    @Query(value = """
    SELECT 
        ug.target_value AS goal,
        ug.progress_value AS readCount,
        CAST(ROUND((ug.progress_value::FLOAT / ug.target_value) * 100) AS INTEGER) AS percentage,
        true AS hasGoal
    FROM UserGoal ug
    JOIN GoalType gt ON ug.goal_type_id = gt.type_id
    WHERE ug.user_id = :userId
    AND gt.name = 'Годовая цель'
    AND EXTRACT(YEAR FROM ug.deadline) = EXTRACT(YEAR FROM CURRENT_DATE)
    """, nativeQuery = true)
    Optional<GoalProgress> findReadingGoalProgress(@Param("userId") Long userId);


    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.user.userId = :userId AND ub.category.name = 'Прочитано'")
    int countReadBooks(@Param("userId") Long userId);


}
