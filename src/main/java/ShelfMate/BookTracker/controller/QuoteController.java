package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.QuoteForm;
import ShelfMate.BookTracker.model.Quote;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.service.BookService;
import ShelfMate.BookTracker.service.QuoteService;
import ShelfMate.BookTracker.service.UserService;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Data
@RequestMapping("/quotes")
public class QuoteController {
    private final QuoteService quoteService;
    private final BookService bookService;
    private final UserService userService;

    @GetMapping
    public String showAllQuotes(Model model) {
        model.addAttribute("quotes", quoteService.getAllQuotes());
        return "quotes"; // quotes.html
    }

    @GetMapping("/add")
    public String showAddQuoteForm(Model model) {
        model.addAttribute("quote", new QuoteForm());
        model.addAttribute("books", bookService.getAllBooks());
        return "add-quote";
    }

    @PostMapping("/add")
    public String addQuote(@ModelAttribute("quote") QuoteForm form, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            quoteService.saveQuote(form, user);
        }
        return "redirect:/quotes";
    }

    @GetMapping("/edit/{id}")
    public String editQuoteForm(@PathVariable Long id, Model model, Authentication authentication) {
        Quote quote = quoteService.getQuoteById(id);
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            if (!quote.getUser().equals(user)) {
                return "redirect:/quotes";
            }
            model.addAttribute("quote", quote);
            model.addAttribute("books", bookService.getAllBooks());
        }
        return "quotes/edit-quote";
    }

    @PostMapping("/edit/{id}")
    public String updateQuote(@PathVariable Long id, @ModelAttribute Quote updatedQuote, Authentication authentication) {
        Quote quote = quoteService.getQuoteById(id);
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            if (!quote.getUser().equals(user)) {
                return "redirect:/quotes";
            }
            quote.setQuoteText(updatedQuote.getQuoteText());
            quote.setBook(updatedQuote.getBook());
            QuoteForm form = new QuoteForm();
            form.setQuoteText(updatedQuote.getQuoteText());
            quoteService.updateQuote(quote);
        }
        return "redirect:/quotes";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuote(@PathVariable Long id, Authentication authentication) {
        Quote quote = quoteService.getQuoteById(id);
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            if (quote.getUser().equals(user)) {
                quoteService.deleteQuoteById(id);
            }
        }
        return "redirect:/quotes";
    }

}
