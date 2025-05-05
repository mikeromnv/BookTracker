package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Genre;
import ShelfMate.BookTracker.repository.GenreRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class GenreService {

    private final GenreRepository genreRepository;
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
