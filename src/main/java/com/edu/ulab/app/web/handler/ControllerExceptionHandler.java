package com.edu.ulab.app.web.handler;

import com.edu.ulab.app.exception.*;
import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseWebResponse> handleNotFoundExceptionException(@NonNull final NotFoundException exc) {
        log.error(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    @ExceptionHandler(UserAgeNotSpecifyException.class)
    public ResponseEntity<BaseWebResponse> handleUserAgeNotSpecifyException(@NonNull final UserAgeNotSpecifyException usrNotSpecExc) {
        log.error(usrNotSpecExc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(usrNotSpecExc)));
    }

    @ExceptionHandler(UserDoesNotDeletedException.class)
    public ResponseEntity<BaseWebResponse> handleUserDoesNotDeletedException(@NonNull final UserDoesNotDeletedException usrNotDelExc) {
        log.error(usrNotDelExc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(usrNotDelExc)));
    }

    @ExceptionHandler(BooksDoesNotDeletedException.class)
    public ResponseEntity<BaseWebResponse> handleBooksDoesNotDeletedException(@NonNull final BooksDoesNotDeletedException bookNotDelExc) {
        log.error(bookNotDelExc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(bookNotDelExc)));
    }

    @ExceptionHandler(UserBooksDoesNotDeletedException.class)
    public ResponseEntity<BaseWebResponse> handleUserBooksDoesNotDeletedException(@NonNull final UserBooksDoesNotDeletedException userBookNotDelExc) {
        log.error(userBookNotDelExc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseWebResponse(createErrorMessage(userBookNotDelExc)));
    }


    private String createErrorMessage(Exception exception) {
        final String message = exception.getMessage();
        log.error(ExceptionHandlerUtils.buildErrorMessage(exception));
        return message;
    }
}
