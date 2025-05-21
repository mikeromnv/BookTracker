package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.service.QuoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public String showAllQuotes(Model model) {
        model.addAttribute("quotes", quoteService.getAllQuotes());
        return "quotes"; // quotes.html
    }
}
