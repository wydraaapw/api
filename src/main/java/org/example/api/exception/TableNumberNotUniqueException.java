package org.example.api.exception;

public class TableNumberNotUniqueException extends RuntimeException {
    public TableNumberNotUniqueException(String message) {
        super(message);
    }
}
