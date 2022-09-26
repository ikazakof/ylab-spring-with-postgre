package com.edu.ulab.app.exception;

public class BooksDoesNotDeletedException extends RuntimeException {
    public BooksDoesNotDeletedException(String message) {
        super(message);
    }
}
