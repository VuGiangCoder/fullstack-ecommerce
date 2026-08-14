package mz.co.mvt.ecommerce.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mz.co.mvt.ecommerce.config.AppConstant;
import mz.co.mvt.ecommerce.payload.CategoryDTO;
import mz.co.mvt.ecommerce.payload.CategoryResponse;
import mz.co.mvt.ecommerce.service.CategoryServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
        @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
        @RequestParam(value = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_CATEGORY_BY, required = false) String sortBy,
        @RequestParam(value = "sortOrder", defaultValue = AppConstant.DEFAULT_SORT_CATEGORY_ORDER_BY, required = false) String sortOrder
    ) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return categoryResponse.getContent().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categoryResponse);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.addCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable UUID categoryId){
            CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(deletedCategory);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable UUID categoryId) {
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategoryDTO);
    }
}
