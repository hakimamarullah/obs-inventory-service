package com.sg.obs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    @Builder.Default
    private Integer code = 200;
    private String message;
    private T data;



    public static <U> ApiResponse<U> setSuccess(U data, String message, int code) {
        return ApiResponse.<U>builder()
                .code(code)
                .data(data)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> setSuccess(String message) {
        return setSuccess(null, message, 200);
    }

    public static <U> ApiResponse<U> setSuccess() {
        return setSuccess(null, "Success", 200);
    }

    public static <U> ApiResponse<U> setSuccess(U data, int code) {
        return setSuccess(data, "Success", code);
    }

    public static <U> ApiResponse<U> setSuccess(U data) {
        return setSuccess(data, "Success", 200);
    }




    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(this.code);
    }
}
