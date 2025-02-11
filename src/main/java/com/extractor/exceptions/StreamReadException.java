package com.extractor.exceptions;

public class StreamReadException extends RuntimeException {
    public StreamReadException(String message) {
        super(message);
    }

  public StreamReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public StreamReadException(Throwable cause) {
    super(cause);
  }

  public StreamReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public StreamReadException() {
  }
}
