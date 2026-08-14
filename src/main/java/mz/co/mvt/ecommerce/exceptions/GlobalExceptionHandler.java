package mz.co.mvt.ecommerce.exceptions;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // 1. NGHIỆP VỤ (CUSTOM BUSINESS EXCEPTIONS)
    // =========================================================================

    /**
     * Lỗi 404: Không tìm thấy tài nguyên (User, Product, Category...)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Lỗi 400: Vi phạm logic nghiệp vụ (Ví dụ: Số lượng tồn kho không đủ, tài khoản bị khóa)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Lỗi 409: Xung đột dữ liệu (Ví dụ: Email/Username/CategoryName đã tồn tại)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // =========================================================================
    // 2. VALIDATION & REQUEST INPUT (LỖI THAM SỐ ĐẦU VÀO)
    // =========================================================================

    /**
     * Lỗi 400: Validation thất bại khi dùng @Valid / @Validated trên @RequestBody DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input");
        response.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Lỗi 400: Validation thất bại trên @PathVariable hoặc @RequestParam (dùng @Validated ở Controller)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Lỗi 400: Định dạng JSON gửi lên bị sai (Malformed JSON) hoặc thiếu Body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Malformed JSON");
        response.put("message", "Input Json is not valid");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Lỗi 400: Sai kiểu dữ liệu Param trên URL (Ví dụ: truyền ?id=abc trong khi controller nhận Long id)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Type Mismatch");
        response.put("message", String.format("Invalid value '%s' for parameter '%s'.", ex.getName(), ex.getValue()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Lỗi 400: Thiếu @RequestParam bắt buộc trên URL
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Missing Parameter");
        response.put("message", String.format("Required parameter '%s' is missing: '%s'", ex.getParameterName()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // =========================================================================
    // 3. HTTP PROTOCOL & ROUTING (LỖI ROUTING & METHOD)
    // =========================================================================

    /**
     * Lỗi 405: Gọi sai HTTP Method (Ví dụ: Endpoint chỉ chấp nhận POST nhưng client gọi GET)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        response.put("error", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        response.put("message", String.format("HTTP method '%s' is not supported.", ex.getMethod()));

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Lỗi 415: Sai Content-Type (Ví dụ: API cần application/json nhưng client gửi text/plain)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        response.put("error", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        response.put("message", String.format("Content-Type '%s' is not supported.", ex.getContentType()));

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * Lỗi 404: Endpoint không tồn tại trong hệ thống (Spring Boot 3+)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        response.put("message", String.format("Endpoint '%s' is not exist.", ex.getResourcePath()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // =========================================================================
    // 4. DATABASE & DATABASE CONSTRAINTS
    // =========================================================================

    /**
     * Lỗi 409: Vi phạm Ràng buộc Database (Unique Constraint, Foreign Key Constraint)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Database Constraint Violation");
        response.put("message", "Operation failed due to a database constraint violation.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // =========================================================================
    // 5. SECURITY & AUTHORIZATION
    // =========================================================================

    /**
     * Lỗi 403: Người dùng đã đăng nhập nhưng không có quyền truy cập (Access Denied)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        response.put("message", "You do not have permission to perform this action.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // =========================================================================
    // 6. CATCH-ALL (TẤT CẢ LỖI KHÔNG MONG MUỐN KHÁC)
    // =========================================================================

    /**
     * Lỗi 500: Lỗi không xác định / Lỗi hệ thống (NullPointerException, OutOfMemory, v.v.)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUncaughtException(Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.put("message", "An internal system error occurred. Please contact the administrator.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}