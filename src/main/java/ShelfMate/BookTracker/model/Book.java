package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "ISBN_num", unique = true, length = 17)
    private String isbnNum;

    private Integer yearPublic;
    private String description;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "cover_url")
    private String coverUrl;

//    @ManyToMany
//    @JoinTable(
//            name = "bookauthor",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "author_id")
//    )
//    private List<Author> authors = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<UserBook> userBooks = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookProgress> progresses = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookAuthor> bookAuthors = new ArrayList<>();

    @Transient // Не сохраняется в БД
    private Long reviewCount;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

}