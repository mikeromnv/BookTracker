package ShelfMate.BookTracker.dto;

import ShelfMate.BookTracker.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorForm {
    private Long authorId;
    private String authorName;
    private String bio;
}
