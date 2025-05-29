package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.*;
import ShelfMate.BookTracker.repository.*;
import ShelfMate.BookTracker.service.*;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class BookProgressService {

    private final BookProgressRepository bookProgressRepository;

    @Transactional
    public void saveOrUpdateProgress(User user, Book book, int currentPage) {
        // Проверка существующего прогресса
        BookProgress progress = bookProgressRepository.findByUserAndBook(user, book)
                .orElse(new BookProgress());

        // Установка значений
        progress.setUser(user);
        progress.setBook(book);
        progress.setTotalPages(book.getPageCount());
        progress.setCurrentPage(currentPage);
        progress.setUpdatedAt(LocalDateTime.now());

        // Сохранение
        bookProgressRepository.save(progress);

    }
    public String getProgress(Book book, User user) {
        return bookProgressRepository.findByUserAndBook(user, book)
                .map(p -> p.getCurrentPage() + "/" + p.getTotalPages())
                .orElse("0/" + book.getPageCount());
    }

    public List<BookProgress> getByUser(User user) {
        return bookProgressRepository.findByUser(user).stream()
                .sorted(Comparator.comparing(bp -> bp.getBook().getBookId()))
                .collect(Collectors.toList());
    }


    public void updateProgress(User user, Book book, int currentPage) {
        BookProgress progress = bookProgressRepository
                .findByUserAndBook(user, book)
                .orElseGet(() -> new BookProgress(user, book, book.getPageCount(), currentPage, LocalDateTime.now()));

        progress.setTotalPages(book.getPageCount());
        progress.setCurrentPage(currentPage);
        progress.setUpdatedAt(LocalDateTime.now());

        bookProgressRepository.save(progress);
    }



}
