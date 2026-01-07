package org.example.api.exception;

public class IncorrectReservationDateException extends RuntimeException {
    public IncorrectReservationDateException(String message) {
        super(message);
    }
}
