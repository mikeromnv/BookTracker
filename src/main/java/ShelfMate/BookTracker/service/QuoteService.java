package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.Quote;
import ShelfMate.BookTracker.repository.QuoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAllByOrderByCreatedAtDesc();
    }
}

