package ShelfMate.BookTracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookprogress")
@IdClass(BookProgressId.class) // Составной ключ
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookProgress {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "total_pages", nullable = false)
    private Integer totalPages;

    @Column(name = "current_page", nullable = false)
    private Integer currentPage = 0;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

