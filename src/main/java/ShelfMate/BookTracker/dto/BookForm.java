package ShelfMate.BookTracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BookForm {

    private String title;
    private String isbnNum;
    private Integer yearPublic;
    private String description;
    private Integer pageCount;
    private transient MultipartFile coverImage;
    private Long genreId;
    private List<Long> authorIds;

}