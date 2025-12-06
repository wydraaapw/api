package org.example.api.exception;

public class ChangePasswordOldPasswordWrongException extends RuntimeException {
    public ChangePasswordOldPasswordWrongException(String message) {
        super(message);
    }
}
