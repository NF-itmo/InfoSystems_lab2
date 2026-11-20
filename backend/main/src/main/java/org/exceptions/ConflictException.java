package org.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException() {
        super("ConflictException");
    }
}
