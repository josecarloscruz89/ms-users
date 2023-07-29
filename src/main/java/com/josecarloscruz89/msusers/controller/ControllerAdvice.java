package com.josecarloscruz89.msusers.controller;

import com.josecarloscruz89.msusers.exception.NotFoundException;
import com.josecarloscruz89.msusers.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundException(NotFoundException notFoundException) {
        log.error("Not found exception happened.", notFoundException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponse> errors = new ArrayList<>();
        e.getAllErrors().forEach(error -> {
            errors.add(new ErrorResponse(error.getDefaultMessage()));
        });

        return errors;
    }
}