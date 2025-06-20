package com.sg.obs.exception;

public class ApiException extends RuntimeException {

    private int httpCode;

    public ApiException(int httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public ApiException(String message) {
        super(message);
        this.httpCode = 500;
    }
    public int getHttpCode() {
        return httpCode;
    }
}
