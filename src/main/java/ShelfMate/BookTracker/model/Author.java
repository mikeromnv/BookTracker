package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @Column(name = "author_name", nullable = false, length = 100)
    private String authorName;

    @Column(length = 2000)
    private String bio;

    @OneToMany(mappedBy = "author")
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}