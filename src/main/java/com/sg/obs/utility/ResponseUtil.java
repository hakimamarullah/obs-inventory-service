package com.sg.obs.utility;

import com.sg.obs.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<ApiResponse<T>> build(ApiResponse<T> response) {
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
