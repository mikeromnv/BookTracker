package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.dto.RegisterRequest;
import ShelfMate.BookTracker.model.User;
import ShelfMate.BookTracker.model.UserRole;
import ShelfMate.BookTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        // например, по умолчанию роль USER
        user.setRole(new UserRole("USER"));
        userRepo.save(user);
    }
}

