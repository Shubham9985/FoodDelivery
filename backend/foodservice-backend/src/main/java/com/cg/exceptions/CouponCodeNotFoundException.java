package com.cg.exceptions;

public class CouponCodeNotFoundException extends RuntimeException {

    public CouponCodeNotFoundException(String message) {
        super(message);
    }
}