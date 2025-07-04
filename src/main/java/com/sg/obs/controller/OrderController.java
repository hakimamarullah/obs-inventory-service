package com.sg.obs.controller;

import com.sg.obs.annotations.LogRequestResponse;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.order.CreateOrderRequest;
import com.sg.obs.dto.order.OrderInfo;
import com.sg.obs.dto.order.UpdateOrderRequest;
import com.sg.obs.service.OrderService;
import com.sg.obs.utility.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@LogRequestResponse
public class OrderController {

    private final OrderService orderService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all orders with pagination")
    public ResponseEntity<ApiResponse<PageWrapper<OrderInfo>>> getOrders(@ParameterObject Pageable pageable) {
        return ResponseUtil.build(orderService.getOrderList(pageable));
    }

    @GetMapping(value = "/{orderNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order by order number")
    public ResponseEntity<ApiResponse<OrderInfo>> getOrderByNo(@PathVariable String orderNo) {
        return ResponseUtil.build(orderService.getOrderByOrderNo(orderNo));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new order")
    public ResponseEntity<ApiResponse<OrderInfo>> addOrder(@RequestBody @Valid CreateOrderRequest payload) {
        return ResponseUtil.build(orderService.createOrder(payload));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update existing order")
    public ResponseEntity<ApiResponse<OrderInfo>> updateOrder(@RequestBody @Valid UpdateOrderRequest payload) {
        return ResponseUtil.build(orderService.updateOrder(payload));
    }

    @DeleteMapping(value = "/{orderNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete order by order number")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable String orderNo) {
        return ResponseUtil.build(orderService.deleteOrderByOrderNo(orderNo));
    }
}

