package com.sg.obs.exception;

import java.util.Optional;

public class DataNotFoundException extends ApiException{

    public DataNotFoundException(String message) {
        super(404, Optional.ofNullable(message).orElse("Data not found"));
    }


}
