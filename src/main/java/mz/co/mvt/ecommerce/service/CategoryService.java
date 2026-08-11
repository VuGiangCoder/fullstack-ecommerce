package mz.co.mvt.ecommerce.service;

import java.util.List;

import mz.co.mvt.ecommerce.model.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    void addCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Long categoryId, Category category);
}
