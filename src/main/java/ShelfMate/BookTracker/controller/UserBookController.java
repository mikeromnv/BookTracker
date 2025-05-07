//package ShelfMate.BookTracker.controller;
//
//import ShelfMate.BookTracker.model.Category;
//import ShelfMate.BookTracker.model.User;
//import ShelfMate.BookTracker.model.UserBook;
//import ShelfMate.BookTracker.service.BookService;
//import ShelfMate.BookTracker.service.CategoryService;
//import ShelfMate.BookTracker.service.UserBookService;
//import ShelfMate.BookTracker.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.security.Principal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Controller
//@RequiredArgsConstructor
//
//public class UserBookController {
//    private final UserBookService userBookService;
//    private final CategoryService categoryService;
//    private final UserService userService;
//    private final BookService bookService;
//
//    @PostMapping("/books/addToCategory")
//    public String addBookToCategory(@RequestParam("bookId") Long bookId,
//                                    @RequestParam("category") String categoryName,
//                                    Principal principal) {
//        User user = userService.findByUsername(principal.getName());
//        Category category = categoryService.findByName(categoryName);
//
//
//
//        // Проверка, существует ли уже запись
//        Optional<UserBook> existing = userBookService.findByUserAndBook(user, book);
//
//        if (existing != null) {
//            existing.setCategory(category); // обновить категорию
//            existing.setAddedAt(LocalDateTime.now());
//            userBookService.save(existing);
//        } else {
//            UserBook newEntry = new UserBook();
//            newEntry.setUser(user);
//            newEntry.setBook(bookService.getBookById(bookId));
//            newEntry.setCategory(category);
//            newEntry.setAddedAt(LocalDateTime.now());
//            userBookService.save(newEntry);
//        }
//
//        return "redirect:/books";
//    }
//
//}
