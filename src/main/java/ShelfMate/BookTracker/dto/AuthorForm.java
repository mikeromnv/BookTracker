package ShelfMate.BookTracker.dto;

import ShelfMate.BookTracker.model.User;
import lombok.Data;

import java.util.List;

@Data
public class AuthorForm {
    private Long authorId;
    private String authorName;
    private String bio;
}
