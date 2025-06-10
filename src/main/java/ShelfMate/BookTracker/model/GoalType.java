package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goaltype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long typeId;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    private String description;

    @OneToMany(mappedBy = "goalType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGoal> userGoals = new ArrayList<>();

}