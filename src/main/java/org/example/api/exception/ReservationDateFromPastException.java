package org.example.api.exception;

public class ReservationDateFromPastException extends RuntimeException {
    public ReservationDateFromPastException(String message) {
        super(message);
    }
}
