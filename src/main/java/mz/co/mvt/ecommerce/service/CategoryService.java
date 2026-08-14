package mz.co.mvt.ecommerce.service;

import java.util.UUID;

import mz.co.mvt.ecommerce.model.Category;
import mz.co.mvt.ecommerce.payload.CategoryDTO;
import mz.co.mvt.ecommerce.payload.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    
    CategoryDTO deleteCategory(UUID categoryId);
    
    CategoryDTO updateCategory(UUID categoryId, CategoryDTO categoryDTO);
}
