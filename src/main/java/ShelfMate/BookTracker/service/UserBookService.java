//package ShelfMate.BookTracker.service;
//
//import ShelfMate.BookTracker.model.Book;
//import ShelfMate.BookTracker.model.Category;
//import ShelfMate.BookTracker.model.User;
//import ShelfMate.BookTracker.model.UserBook;
//import ShelfMate.BookTracker.repository.BookRepository;
//import ShelfMate.BookTracker.repository.CategoryRepository;
//import ShelfMate.BookTracker.repository.UserBookRepository;
//import ShelfMate.BookTracker.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Data
//public class UserBookService {
//    private final UserRepository userRepository;
//    private final BookRepository bookRepository;
//    private final UserBookRepository userBookRepository;
//    private final CategoryRepository categoryRepository;
//
//    @Transactional
//    public void addBookToUserCategory(String username, Long bookId, Long categoryId) {
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
//
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//        // Проверяем, есть ли уже книга у пользователя
//        Optional<UserBook> existing = userBookRepository.findByUserAndBook(user, book);
//
//        if (existing.isPresent()) {
//            // Обновляем категорию
//            UserBook userBook = existing.get();
//            userBook.setCategory(category);
//            userBookRepository.save(userBook);
//        } else {
//            // Создаем новую запись
//            UserBook userBook = new UserBook();
//            userBook.setUser(user);
//            userBook.setBook(book);
//            userBook.setCategory(category);
//            userBookRepository.save(userBook);
//        }
//    }
//
////    public UserBook findByUserIdAndBookId(Long userId, Long bookId) {
////        return userBookRepository.findByUserIdAndBookId(userId,bookId)
////                .orElseThrow(() -> new EntityNotFoundException("User not found"));
////    }
//
//    public UserBook save(UserBook userBook) {
//        return userBookRepository.save(userBook);
//    }
//}
