package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "userbooks")
@Data
public class UserBook {

    @EmbeddedId
    private UserBookId id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("book")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "ctg_id", nullable = false)
    private Category category;

//    @Column(name = "added_at", updatable = false)
//    @CreationTimestamp
//    private LocalDateTime addedAt;
    @Column(name = "added_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate addedAt;

}


