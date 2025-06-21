package com.sg.obs.controller;

import com.sg.obs.PageWrapper;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.order.CreateOrderRequest;
import com.sg.obs.dto.order.OrderInfo;
import com.sg.obs.dto.order.UpdateOrderRequest;
import com.sg.obs.service.OrderService;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private OrderService orderService;


    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/v1/orders";
    }

    @Test
    void getOrderByOrderNo_ShouldReturnSuccess() {
        // Mock service
        OrderInfo orderInfo = OrderInfo.builder()
                .orderNo("20250600000001")
                .qty(1)
                .price(23.0)
                .itemName("Item 1")
                .build();

        doReturn(ApiResponse.setSuccess(orderInfo)).when(orderService).getOrderByOrderNo(anyString());

        // Act
        ApiResponse<OrderInfo> response = given().pathParam("orderNo", "20250600000001")
                .accept(ContentType.JSON)
                .when().get("/{orderNo}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(orderInfo));
        verify(orderService).getOrderByOrderNo(anyString());
    }

    @Test
    void getOrderList_ShouldReturnPagedModel() {
        // Mock service
        Page<OrderInfo> page = new PageImpl<>(List.of());
        doReturn(ApiResponse.setSuccess(new PagedModel<>(page))).when(orderService).getOrderList(any(Pageable.class));

        // Act
        ApiResponse<PageWrapper<OrderInfo>> response = given().accept(ContentType.JSON)
                .when().get("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(new PageWrapper<>(new PagedModel<>(page))));
        verify(orderService).getOrderList(any(Pageable.class));
    }

    @Test
    void getOrderList_ShouldReturnEmptyResult() {
        // Mock service
        Page<OrderInfo> page = new PageImpl<>(List.of());
        doReturn(ApiResponse.setSuccess(new PagedModel<>(page))).when(orderService).getOrderList(any(Pageable.class));

        // Act
        ApiResponse<PageWrapper<OrderInfo>> response = given().accept(ContentType.JSON)
                .when().get("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(new PageWrapper<>(new PagedModel<>(page))));
        verify(orderService).getOrderList(any(Pageable.class));
    }

    @Test
    void addOrder_ShouldReturnSuccess() {
        // Mock service
        OrderInfo orderInfo = OrderInfo.builder()
                .orderNo("20250600000001")
                .qty(1)
                .price(23.0)
                .itemName("Item 1")
                .build();

        doReturn(ApiResponse.setResponse(orderInfo, 201)).when(orderService).createOrder(any(CreateOrderRequest.class));

        // Act
        CreateOrderRequest payload = CreateOrderRequest.builder()
                .itemId(1L)
                .qty(1)
                .build();
        ApiResponse<OrderInfo> response = given().contentType(ContentType.JSON)
                .body(payload)
                .when().post("")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setResponse(orderInfo, 201));
        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void givenInvalidCreateOrderRequest_whenAddOrder_thenShouldReturnBadRequest() {
        // Act
        CreateOrderRequest payload = CreateOrderRequest.builder()
                .qty(1)
                .build();
        given().contentType(ContentType.JSON)
                .body(payload)
                .when().post("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidUpdateOrderRequest_whenUpdateOrder_thenShouldReturnBadRequest() {
        // Act
        UpdateOrderRequest payload = UpdateOrderRequest.builder()
                .qty(1)
                .build();
        given().contentType(ContentType.JSON)
                .body(payload)
                .when().put("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidUpdateOrderRequest_whenUpdateOrder_thenShouldReturnSuccess() {
        // Mock service
        OrderInfo orderInfo = OrderInfo.builder()
                .orderNo("20250600000001")
                .qty(1)
                .price(23.0)
                .itemName("Item 1")
                .build();

        doReturn(ApiResponse.setSuccess(orderInfo)).when(orderService).updateOrder(any(UpdateOrderRequest.class));

        // Act
        UpdateOrderRequest payload = UpdateOrderRequest.builder()
                .qty(1)
                .orderNo("20250600000001")
                .build();
        ApiResponse<OrderInfo> response = given().contentType(ContentType.JSON)
                .body(payload)
                .when().put("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(orderInfo));
        verify(orderService).updateOrder(any(UpdateOrderRequest.class));
    }

    @Test
    void deleteOrderByOrderNo_ShouldReturnSuccess() {
        // Mock service
        doReturn(ApiResponse.setSuccessWithMessage("Success delete")).when(orderService).deleteOrderByOrderNo(anyString());

        // Act
        ApiResponse<String> response = given().accept(ContentType.JSON)
                .when().delete("/{orderNo}", "20250600000001")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccessWithMessage("Success delete"));
        verify(orderService).deleteOrderByOrderNo(anyString());
    }


}