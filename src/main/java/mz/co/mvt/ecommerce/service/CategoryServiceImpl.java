package mz.co.mvt.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mz.co.mvt.ecommerce.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    private Long nextCategoryId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void addCategory(Category category) {
        category.setCategoryId(nextCategoryId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId));
        
        categories.remove(category);
        return "Category deleted successfully!";
    }

    @Override
    public Category updateCategory(Long categoryId, Category updatedCategory) {
        Category existingCategory = categories.stream()
                .filter(c -> Objects.equals(c.getCategoryId(), categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId
                ));
        existingCategory.setCategoryName(updatedCategory.getCategoryName());
        return existingCategory;
    }
    
}
