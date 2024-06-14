package com.praful.feedapplication.exception;

public class SqsMessageException extends RuntimeException {
    public SqsMessageException(String msg) {
        super(msg);
    }
}
