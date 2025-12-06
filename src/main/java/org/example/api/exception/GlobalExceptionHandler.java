package org.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_FIELD = "error";
    private static final String MESSAGE_FIELD = "message";


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException bce){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(ERROR_FIELD, "ERR_BAD_CREDENTIALS", MESSAGE_FIELD, "Incorrect email or password"));
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<Map<String, String>> handleEmailTaken(EmailAlreadyTakenException eate){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(ERROR_FIELD, "ERR_EMAIL_TAKEN", MESSAGE_FIELD, eate.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Invalid token"));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Token has expired"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "User not found"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabledException(DisabledException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(ERROR_FIELD, "ERR_ACCOUNT_INACTIVE", MESSAGE_FIELD, "Account is not activated"));
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<Map<String, String>> handleInactiveAccount(InactiveAccountException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Account is not activated"));
    }

    @ExceptionHandler(ChangePasswordOldPasswordWrongException.class)
    public ResponseEntity<Map<String, String>> handleWrongOldPassword(ChangePasswordOldPasswordWrongException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Old password is incorrect"));
    }

    @ExceptionHandler(ActivationMailException.class)
    public ResponseEntity<Map<String, String>> handleActivationMailError(ActivationMailException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Sending activation mail failed"));
    }

    @ExceptionHandler(ResetEmailException.class)
    public ResponseEntity<Map<String, String>> handleResetEmailError(ResetEmailException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(ERROR_FIELD, ex.getMessage(), MESSAGE_FIELD, "Sending password reset mail failed"));
    }



}
