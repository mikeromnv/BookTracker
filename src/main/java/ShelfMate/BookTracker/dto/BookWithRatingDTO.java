package ShelfMate.BookTracker.dto;

import ShelfMate.BookTracker.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookWithRatingDTO {
    private Book book;
    private Double averageRating;
    private Integer reviewCount;

    public String getStarRating() {
        if (averageRating == null) return "Нет оценок";
        return String.format("%.1f ★", averageRating);
    }
}