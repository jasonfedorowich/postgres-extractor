package com.extractor.exceptions;

public class StreamFlushException extends RuntimeException {
    public StreamFlushException(String message) {
        super(message);
    }

    public StreamFlushException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamFlushException(Throwable cause) {
        super(cause);
    }

    public StreamFlushException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StreamFlushException() {
    }
}
