package ShelfMate.BookTracker.service;


import ShelfMate.BookTracker.dto.AuthorForm;
import ShelfMate.BookTracker.dto.BookForm;
import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.repository.AuthorRepository;
import ShelfMate.BookTracker.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

    public AuthorForm convertToForm(Author author) {
        AuthorForm form = new AuthorForm();
        form.setAuthorId(author.getAuthorId());
        form.setAuthorName(author.getAuthorName());
        form.setBio(author.getBio());
        return form;
    }

    @Transactional
    public void updateAuthor(AuthorForm form, User user, Long authorId) throws IOException {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));

        if (!author.getOwner().getEmail().equals(user.getEmail())) {
            throw new SecurityException("Нет прав на редактирование");
        }

        author.setAuthorName(form.getAuthorName());
        author.setBio(form.getBio());
        author.setOwner(user);

       authorRepository.save(author);
    }

}
