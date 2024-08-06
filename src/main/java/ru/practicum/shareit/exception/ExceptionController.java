package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, AvailabilityException.class, BadDateException.class,
            UnsupportedStateException.class, StatusException.class, CommentException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(Exception exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorMessage> handleMethodDuplicateDataException(DuplicateDataException exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(NotOwnerException.class)
    public ResponseEntity<ErrorMessage> handleMethodNotOwnerException(NotOwnerException exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handle(Exception exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
