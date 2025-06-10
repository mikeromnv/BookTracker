package ShelfMate.BookTracker.controller;

import ShelfMate.BookTracker.dto.RegisterRequest;
import ShelfMate.BookTracker.service.AuthorService;
import ShelfMate.BookTracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register"; // register.html
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterRequest request, Model model) {
        try {
            userService.registerUser(request);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
