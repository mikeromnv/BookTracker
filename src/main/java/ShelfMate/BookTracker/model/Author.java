package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Column(nullable = false, length = 100)
    private String authorName;

    private String bio;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();

    // Геттеры, сеттеры, конструкторы
}