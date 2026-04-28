package com.cg.exceptions;

public class PhoneNumberNotFoundException extends RuntimeException {

    public PhoneNumberNotFoundException(String message) {
        super(message);
    }
}