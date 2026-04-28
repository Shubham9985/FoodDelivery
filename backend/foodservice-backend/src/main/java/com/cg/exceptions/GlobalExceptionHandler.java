package com.cg.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ID NOT FOUND
    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleIdNotFound(IdNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // NAME NOT FOUND
    @ExceptionHandler(NameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNameNotFound(NameNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // PHONE NUMBER NOT FOUND
    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePhoneNotFound(PhoneNumberNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // DUPLICATE DATA
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateDataException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // INVALID INPUT
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInput(InvalidInputException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // UNAUTHORIZED
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // ORDER ERROR
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<Map<String, Object>> handleOrder(OrderException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // COUPON ERROR
    @ExceptionHandler(CouponException.class)
    public ResponseEntity<Map<String, Object>> handleCoupon(CouponException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ALL OTHER EXCEPTIONS
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return buildResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}