package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.Book;
import ShelfMate.BookTracker.model.Category;
import ShelfMate.BookTracker.model.UserBook;
import ShelfMate.BookTracker.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBookService {

    private final UserBookRepository userBookRepository;

    public Map<Category, List<Book>> getUserBooksGroupedByCategory(Long userId) {
        List<UserBook> userBooks = userBookRepository.findByUserIdWithCategory(userId);

        return userBooks.stream()
                .filter(ub -> ub.getCategory() != null)
                .collect(Collectors.groupingBy(
                        UserBook::getCategory,
                        Collectors.mapping(UserBook::getBook, Collectors.toList())
                ));
    }
}