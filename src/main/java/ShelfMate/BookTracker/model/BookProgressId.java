package ShelfMate.BookTracker.model;

import java.io.Serializable;

public class BookProgressId implements Serializable {
    private Long user;
    private Long book;

    // equals() и hashCode() обязательно!
}
