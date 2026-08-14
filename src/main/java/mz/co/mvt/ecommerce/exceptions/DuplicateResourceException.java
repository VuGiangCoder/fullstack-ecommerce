package mz.co.mvt.ecommerce.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * DuplicateResourceException
 */

@Getter
@Setter
public class DuplicateResourceException extends RuntimeException {
    private final HttpStatus status;

    public DuplicateResourceException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}
