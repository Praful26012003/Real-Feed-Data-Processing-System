package com.praful.feedapplication.exception.handler;

import com.praful.feedapplication.exception.*;
import com.praful.feedapplication.protos.ApiStatusCode;
//import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    protected ApiStatusCode handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("404").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {SqsMessageException.class})
    protected ApiStatusCode handleSqsMessageException(SqsMessageException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("500").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InsufficientUserBalanceException.class})
    protected ApiStatusCode handleInsufficientUserBalanceException(InsufficientUserBalanceException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("400").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {UserNotAuthorizedException.class})
    protected ApiStatusCode handleUserNotAuthorizedException(UserNotAuthorizedException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("400").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {InternalServerErrorException.class})
    protected ApiStatusCode handleInternalServerException(InternalServerErrorException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("500").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {InvalidInputException.class})
    protected ApiStatusCode handleInvalidInputException(InvalidInputException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("400").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {DuplicateElementException.class})
    protected ApiStatusCode handleDuplicateElementException(DuplicateElementException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("400").setMessage(ex.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {DatabaseException.class})
    protected ApiStatusCode handleDatabaseException(DatabaseException ex) {
        return ApiStatusCode.newBuilder().setStatusCode("400").setMessage(ex.getMessage()).build();
    }
}
