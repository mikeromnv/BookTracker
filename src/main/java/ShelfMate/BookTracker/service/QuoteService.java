package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.dto.QuoteForm;
import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Quote;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.repository.BookRepository;
import ShelfMate.BookTracker.repository.QuoteRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Data
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final BookRepository bookRepository;

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAllByOrderByCreatedAtDesc();
    }

    public void saveQuote(QuoteForm form, User user) {

        Book book = bookRepository.findById(form.getBookId()).orElseThrow();


        Quote quote = new Quote();
        quote.setQuoteText(form.getQuoteText());
        quote.setBook(book);
        quote.setUser(user);
        quote.setCreatedAt(LocalDate.now());

        quoteRepository.save(quote);
    }
    public void updateQuote(Quote quote) {
        quoteRepository.save(quote); // Просто обновляем — ID уже существует
    }

    public List<Quote> getQuotesByUser(User user) {
        return quoteRepository.findAllByUser(user);
    }

    public Quote getQuoteById(Long id) {
        return quoteRepository.findById(id).orElseThrow(() -> new RuntimeException("Цитата не найдена"));
    }

    public void deleteQuoteById(Long id) {
        quoteRepository.deleteById(id);
    }
    public List<Quote> searchQuote(String quoteText, Long bookId) {
        return quoteRepository.findQuotesByFilters(quoteText, bookId);
    }

}

