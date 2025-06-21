package com.sg.obs.utility;

import com.sg.obs.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseUtilTest {

    @Test
    void givenApiResponse_whenGetResponse_thenReturnResponseEntity() {
        // Given
        ApiResponse<String> apiResponse = ApiResponse.setDefaultSuccess();
        ApiResponse<String> badRequest = ApiResponse.setResponse(null, 400);

        // When
        ResponseEntity<ApiResponse<String>> successResponse = ResponseUtil.build(apiResponse);
        ResponseEntity<ApiResponse<String>> badRequestResponse = ResponseUtil.build(badRequest);

        // Then
        assertThat(successResponse).isNotNull();
        assertThat(badRequestResponse).isNotNull();
        assertThat(successResponse.getStatusCode()).isEqualTo(apiResponse.getHttpStatus());
        assertThat(badRequestResponse.getStatusCode()).isEqualTo(badRequest.getHttpStatus());

        assertThat(successResponse.getBody()).usingRecursiveComparison().isEqualTo(apiResponse);
        assertThat(badRequestResponse.getBody()).usingRecursiveComparison().isEqualTo(badRequest);

    }
}