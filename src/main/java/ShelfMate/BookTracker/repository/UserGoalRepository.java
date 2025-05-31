package ShelfMate.BookTracker.repository;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    List<UserGoal> findAllByUserAndGoalTypeNameOrderByStartDateDesc(User user, String goalTypeName);

    boolean existsByUserAndGoalTypeNameAndDeadlineAfter(User user, String goalTypeName, LocalDate currentDate);

    List<UserGoal> findAllByUser(User user);
}
