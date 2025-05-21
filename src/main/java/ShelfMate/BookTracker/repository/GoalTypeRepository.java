package ShelfMate.BookTracker.repository;

import ShelfMate.BookTracker.model.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalTypeRepository extends JpaRepository<GoalType, Long> {
    Optional<GoalType> findByName(String name);
}
