package ShelfMate.BookTracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAuthorId implements Serializable {
    @Column(name = "book_id")
    private Long  bookId;

    @Column(name = "author_id")
    private Long  authorId;
}
