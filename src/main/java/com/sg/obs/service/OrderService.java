package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.order.CreateOrderRequest;
import com.sg.obs.dto.order.OrderInfo;
import com.sg.obs.dto.order.UpdateOrderRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface OrderService {

    ApiResponse<PagedModel<OrderInfo>> getOrderList(Pageable pageable);

    ApiResponse<OrderInfo> getOrderByOrderNo(String orderNo);

    ApiResponse<OrderInfo> createOrder(CreateOrderRequest payload);

    ApiResponse<String> deleteOrderByOrderNo(String orderNo);

    ApiResponse<OrderInfo> updateOrder(UpdateOrderRequest payload);
}
