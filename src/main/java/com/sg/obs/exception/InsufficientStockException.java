package com.sg.obs.exception;

public class InsufficientStockException extends ApiException{

    public InsufficientStockException() {
        super(400, "Insufficient stock");
    }
}
