package ShelfMate.BookTracker.service;


import ShelfMate.BookTracker.model.Author;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
    public List<Author> getRandomAuthors(int count) {
        return authorRepository.findRandomAuthors(count);
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }


    @Transactional
    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }
}
