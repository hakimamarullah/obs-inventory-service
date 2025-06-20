package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.enums.InventoryType;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.order.CreateOrderRequest;
import com.sg.obs.dto.order.OrderInfo;
import com.sg.obs.dto.order.UpdateOrderRequest;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.exception.InsufficientStockException;
import com.sg.obs.models.Inventory;
import com.sg.obs.models.Item;
import com.sg.obs.models.Order;
import com.sg.obs.repository.InventoryRepository;
import com.sg.obs.repository.ItemRepository;
import com.sg.obs.repository.OrderRepository;
import com.sg.obs.service.OrderService;
import com.sg.obs.utility.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderSvc implements OrderService {


    public static final String ORDER_WITH_ID_D_NOT_FOUND = "Order with orderNo %s not found";

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final ObjectMapper mapper;


    @Transactional(readOnly = true)
    @Override
    public ApiResponse<PagedModel<OrderInfo>> getOrderList(Pageable pageable) {
        Page<OrderInfo> orders = orderRepository.findAll(pageable)
                .map(this::convertToOrderInfo);
        return ApiResponse.setSuccess(new PagedModel<>(orders));
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderInfo> getOrderByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderRepository.findById(orderNo)
                .map(this::convertToOrderInfo)
                .orElseThrow(() -> new DataNotFoundException(String.format(ORDER_WITH_ID_D_NOT_FOUND, orderNo)));
        return ApiResponse.setSuccess(orderInfo);
    }

    @Transactional
    @Modifying
    @Override
    public ApiResponse<OrderInfo> createOrder(CreateOrderRequest payload) {
        Item item = itemRepository.findById(payload.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Failed to create order. Item not found"));

        if (Objects.isNull(item.getInventory())) {
            throw new DataNotFoundException("Failed to create order. Item inventory not found");
        }

        if (item.getRemainingStock() < payload.getQty()) {
            throw new InsufficientStockException();
        }


        Order order = new Order();
        order.setOrderNo(OrderUtil.generateOrderNo(orderRepository.getNextOrderSeq()));
        order.setItem(item);
        order.setQty(payload.getQty());
        order.setPrice(item.getPrice());

        Order savedOrder = orderRepository.save(order);

        Inventory withdrawal = new Inventory();
        withdrawal.setItem(item);
        withdrawal.setQuantity(payload.getQty());
        withdrawal.setType(InventoryType.W);
        inventoryRepository.save(withdrawal);
        return ApiResponse.setSuccess(convertToOrderInfo(savedOrder));
    }

    @Transactional
    @Modifying
    @Override
    public ApiResponse<String> deleteOrderByOrderNo(String orderNo) {
        int count = orderRepository.removeByOrderNo(orderNo);
        if (count == 0) {
            throw new DataNotFoundException(String.format(ORDER_WITH_ID_D_NOT_FOUND, orderNo));
        }
        var response = new ApiResponse<String>();
        response.setMessage(String.format("Order with orderNo %s deleted successfully", orderNo));
        return response;
    }

    @Transactional
    @Modifying
    @Override
    public ApiResponse<OrderInfo> updateOrder(UpdateOrderRequest payload) {
        Order order = orderRepository.findById(payload.getOrderNo())
                .orElseThrow(() -> new DataNotFoundException(String.format(ORDER_WITH_ID_D_NOT_FOUND, payload.getOrderNo())));

        if (payload.getItemId() != null) {
            Item item = itemRepository.findById(payload.getItemId())
                    .orElseThrow(() -> new DataNotFoundException("Item not found"));
            order.setItem(item);
        }
        
        int remainingStock = Optional.ofNullable(order.getItem()).map(Item::getRemainingStock).orElse(0);
        if (payload.getQty() > remainingStock) {
            throw new InsufficientStockException();
        }
        order.setQty(payload.getQty());
        order.setPrice(payload.getPrice());
        Order updated = orderRepository.save(order);
        return ApiResponse.setSuccess(convertToOrderInfo(updated));
    }

    private OrderInfo convertToOrderInfo(Order order) {
        return mapper.convertValue(order, OrderInfo.class);
    }


}
