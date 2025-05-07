package ShelfMate.BookTracker.service;

import ShelfMate.BookTracker.model.Category;
import ShelfMate.BookTracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Категория с именем " + name + " не найдена"));
    }

}
