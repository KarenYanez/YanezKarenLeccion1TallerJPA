package com.example.supporttickets.exception;

public class InvalidFilterException extends RuntimeException {
    
    public InvalidFilterException(String message) {
        super(message);
    }
    
    public InvalidFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}
