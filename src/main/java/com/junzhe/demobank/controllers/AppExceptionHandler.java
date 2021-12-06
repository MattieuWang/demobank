package com.junzhe.demobank.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value
            = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleIllegalArgs(
            RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, generateError(HttpStatus.BAD_REQUEST, ex, request),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleInternalError(
            RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, generateError(HttpStatus.INTERNAL_SERVER_ERROR, ex, request),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ApiError generateError(HttpStatus status, RuntimeException ex, WebRequest request) {
        String msg = ex.getMessage().contains(":") ? ex.getMessage().substring(ex.getMessage().indexOf(":")) : ex.getMessage();
        return new ApiError(status, msg, ((ServletWebRequest)request).getRequest().getRequestURI());
    }

}

class ApiError {
    private HttpStatus status;
    private String message;
    private Date timestamp;
    private String uri;

    public ApiError(HttpStatus status, String message, String uri) {
        this.status = status;
        this.message = message;
        this.uri = uri;
        this.timestamp = new Date();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
