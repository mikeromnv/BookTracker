package ShelfMate.BookTracker.model;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBookId implements Serializable {
    private Long user;
    private Long book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBookId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, book);
    }
}
