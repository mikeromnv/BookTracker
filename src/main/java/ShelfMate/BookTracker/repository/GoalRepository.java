package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.GoalProgress;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<UserGoal, Long> {

    @Query("SELECT g FROM UserGoal g WHERE g.user = :user AND g.goalType.typeId = :typeId AND g.deadline >= CURRENT_DATE ORDER BY g.deadline ASC")
    Optional<UserGoal> findActualGoalByUserAndGoalType(@Param("user") User user, @Param("typeId") Long typeId);


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
      AND CURRENT_DATE BETWEEN ug.start_date AND ug.deadline
    LIMIT 1
    """, nativeQuery = true)
    Optional<GoalProgress> findReadingGoalProgress(@Param("userId") Long userId);



    @Query("SELECT COUNT(ub) FROM UserBook ub WHERE ub.user.userId = :userId AND ub.category.name = 'Прочитано'")
    int countReadBooks(@Param("userId") Long userId);

    @Query("""
    SELECT ug FROM UserGoal ug 
    JOIN ug.goalType gt 
    WHERE ug.user = :user 
      AND gt.name = :goalTypeName 
      AND CURRENT_DATE BETWEEN ug.startDate AND ug.deadline
    """)
    Optional<UserGoal> findByUserAndGoalTypeName(@Param("user") User user, @Param("goalTypeName") String goalTypeName);


    @Query("SELECT ug FROM UserGoal ug WHERE ug.user = :user AND ug.goalType.typeId = :goalTypeId")
    Optional<UserGoal> findByUserAndGoalType(@Param("user") User user, @Param("goalTypeId") Long goalTypeId);
}
