package com.extractor.exceptions;

public class StreamStartException extends RuntimeException{

    public StreamStartException() {
    }

    public StreamStartException(String message) {
        super(message);
    }

    public StreamStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamStartException(Throwable cause) {
        super(cause);
    }

    public StreamStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
