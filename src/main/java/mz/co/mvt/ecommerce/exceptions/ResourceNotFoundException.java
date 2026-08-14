package mz.co.mvt.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * ResourceNotFoundException
 */

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public ResourceNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

}
