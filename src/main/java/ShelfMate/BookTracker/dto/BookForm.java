package ShelfMate.BookTracker.dto;

import ShelfMate.BookTracker.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BookForm {
    private Long bookId;
    private String title;
    private String isbnNum;
    private Integer yearPublic;
    private String description;
    private Integer pageCount;
    private String coverImage;
    private Long genreId;
    private List<Long> authorIds;
    private User owner;
}