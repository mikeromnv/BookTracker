package ShelfMate.BookTracker.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookForm {
    private String title;
    private Long genreId;
    private String authors; // ФИО авторов через запятую
    private String isbn;
    private Integer year;
    private Integer pageCount;
    private String description;
    private MultipartFile coverImage;
}