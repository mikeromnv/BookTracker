package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goaltype") // Имя таблицы в БД
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id") // Соответствие имени столбца в БД
    private Long typeId;

    @Column(nullable = false, unique = true, length = 20) // Ограничения из БД
    private String name;

    private String description; // Добавлено поле description из схемы БД

    @OneToMany(mappedBy = "goalType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGoal> userGoals = new ArrayList<>();

}