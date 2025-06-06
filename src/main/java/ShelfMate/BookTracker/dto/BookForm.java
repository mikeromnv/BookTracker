package ShelfMate.BookTracker.dto;

import ShelfMate.BookTracker.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BookForm {
    private Long bookId;

    @NotBlank(message = "Название обязательно")
    private String title;

    @Size(max = 17, message = "ISBN не должен превышать 17 символов")
    @NotBlank(message = "ISBN обязателен")
    @Pattern(regexp = "^[\\d-]+$", message = "ISBN должен содержать только цифры и символ '-'")
    private String isbnNum;

    @NotNull(message = "Год публикации обязателен")
    private Integer yearPublic;

    @NotBlank(message = "Описание обязательно")
    private String description;


    @NotNull(message = "Количество страниц обязательно")
    @Min(value = 1, message = "Количество страниц должно быть больше 0")
    private Integer pageCount;


    private String coverImage;
    private Long genreId;

    @NotEmpty(message = "Должен быть указан хотя бы один автор")
    private List<Long> authorIds;

    private User owner;
}