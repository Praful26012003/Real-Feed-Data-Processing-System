package com.praful.feedapplication.exception;

public class InsufficientUserBalanceException extends RuntimeException {
    public InsufficientUserBalanceException(String message) {
        super(message);
    }
}
