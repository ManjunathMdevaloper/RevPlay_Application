package com.revature.revplay.exception;

public class RevPlayException extends RuntimeException {
    public RevPlayException(String message) {
        super(message);
    }
}

// Sub-exceptions
class UserNotFoundException extends RevPlayException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

class UnauthorizedException extends RevPlayException {
    public UnauthorizedException(String message) {
        super(message);
    }
}