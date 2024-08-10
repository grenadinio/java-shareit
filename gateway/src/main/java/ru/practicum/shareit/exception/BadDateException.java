package ru.practicum.shareit.exception;

public class BadDateException extends RuntimeException {
    public BadDateException(String message) {
        super(message);
    }
}
