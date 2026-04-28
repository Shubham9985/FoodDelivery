package com.cg.exceptions;

public class NameNotFoundException extends RuntimeException {

    public NameNotFoundException(String message) {
        super(message);
    }
}