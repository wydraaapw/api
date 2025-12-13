package org.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    private ProblemDetail buildProblemDetail(HttpStatus status, String error, String detail, String title){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty(ERROR_FIELD, error);
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException bce){
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "ERR_BAD_CREDENTIALS", "Nieprawidłowy email lub hasło",
                "Invalid credentials");
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ProblemDetail handleEmailTaken(EmailAlreadyTakenException eate){
        return buildProblemDetail(HttpStatus.CONFLICT, "ERR_EMAIL_TAKEN", eate.getMessage(), "Email Taken");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "ERR_INVALID_TOKEN", ex.getMessage(), "Invalid Token");
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ProblemDetail handleTokenExpired(TokenExpiredException ex) {
        return buildProblemDetail(HttpStatus.GONE, "ERR_TOKEN_EXPIRED", ex.getMessage(), "Token Expired");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, "ERR_USER_NOT_FOUND", ex.getMessage(), "User Not Found");
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabledException(DisabledException ex) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "ERR_ACCOUNT_INACTIVE", "Konto nieaktywne. Aktywuj konto klikając w link wysłany na adres email", "Account Disabled");
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ProblemDetail handleInactiveAccount(InactiveAccountException ex) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "ERR_ACCOUNT_INACTIVE", ex.getMessage(), "Account Inactive");
    }

    @ExceptionHandler(ChangePasswordOldPasswordWrongException.class)
    public ProblemDetail handleWrongOldPassword(ChangePasswordOldPasswordWrongException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "ERR_CHANGE_PASSWORD", ex.getMessage(), "Invalid Password");
    }

    @ExceptionHandler(ActivationMailException.class)
    public ProblemDetail handleActivationMailError(ActivationMailException ex) {
        return buildProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, "ERR_ACTIVATION_MAIL", ex.getMessage(), "Mail Service Error");
    }

    @ExceptionHandler(ResetEmailException.class)
    public ProblemDetail handleResetEmailError(ResetEmailException ex) {
        return buildProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, "ERR_RESET_EMAIL", ex.getMessage(), "Mail Service Error");
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleResourceAlreadyExists(ResourceAlreadyExistsException rae){
        return buildProblemDetail(HttpStatus.CONFLICT, "ERR_RESOURCE_EXISTS", rae.getMessage(), "Resource Already Exists");
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ProblemDetail handleResourceInUse(ResourceInUseException riue){
        return buildProblemDetail(HttpStatus.CONFLICT, "ERR_RESOURCE_IN_USE", riue.getMessage(), "Resource in use");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, "ERR_RESOURCE_NOT_FOUND", ex.getMessage(), "Resource Not Found");
    }

}