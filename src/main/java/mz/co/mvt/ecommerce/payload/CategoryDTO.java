package mz.co.mvt.ecommerce.payload;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {

    private UUID categoryId;
    
    @NotBlank()
    private String categoryName;
}
