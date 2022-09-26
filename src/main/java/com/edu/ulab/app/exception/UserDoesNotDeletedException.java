package com.edu.ulab.app.exception;

public class UserDoesNotDeletedException extends RuntimeException {
    public UserDoesNotDeletedException(String message) {
        super(message);
    }
}
