package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.order.CreateOrderRequest;
import com.sg.obs.dto.order.OrderInfo;
import com.sg.obs.dto.order.UpdateOrderRequest;
import com.sg.obs.enums.InventoryType;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderSvc implements OrderService {


    public static final String ORDER_NOT_FOUND = "Order with orderNo %s not found";

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
                .orElseThrow(() -> new DataNotFoundException(String.format(ORDER_NOT_FOUND, orderNo)));
        return ApiResponse.setSuccess(orderInfo);
    }

    @Transactional
    @Modifying
    @Override
    public ApiResponse<OrderInfo> createOrder(CreateOrderRequest payload) {
        Item item = itemRepository.findById(payload.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Failed to create order. Item not found"));

        if (item.getInventory().isEmpty()) {
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
        return ApiResponse.setSuccess(convertToOrderInfo(savedOrder), 201);
    }

    @Transactional
    @Modifying
    @Override
    public ApiResponse<String> deleteOrderByOrderNo(String orderNo) {
        int count = orderRepository.removeByOrderNo(orderNo);
        if (count == 0) {
            throw new DataNotFoundException(String.format(ORDER_NOT_FOUND, orderNo));
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
                .orElseThrow(() -> new DataNotFoundException(String.format(ORDER_NOT_FOUND, payload.getOrderNo())));

        boolean isItemChanged = false;
        Item oldItem = order.getItem();
        int oldQty = order.getQty();
        long oldItemId = Optional.ofNullable(order.getItem()).map(Item::getId).orElse(0L);
        boolean isQtyChanged = !Objects.equals(order.getQty(), payload.getQty());
        if (payload.getItemId() != null) {
           Item item = itemRepository.findById(payload.getItemId())
                    .orElseThrow(() -> new DataNotFoundException("Item not found"));
            order.setItem(item);
            isItemChanged = item.getId() != oldItemId;
        }

        int remainingStock = Optional.ofNullable(order.getItem()).map(Item::getRemainingStock).orElse(0);
        if (isQtyChanged || isItemChanged) {
            int requiredStock = payload.getQty();
            int stockCheck = isQtyChanged && !isItemChanged
                    ? requiredStock - oldQty
                    : requiredStock;

            if (stockCheck > remainingStock) {
                throw new InsufficientStockException();
            }

            Inventory reversal = topUpInventory(oldItem, oldQty);
            Inventory withdrawal = withdrawalInventory(order.getItem(), payload.getQty());
            inventoryRepository.saveAll(List.of(reversal, withdrawal));
        }

        order.setQty(payload.getQty());

        double storedPrice = isItemChanged ? order.getItem().getPrice() : order.getPrice();
        order.setPrice(Optional.ofNullable(payload.getPrice()).orElse(storedPrice));
        Order updated = orderRepository.save(order);
        return ApiResponse.setSuccess(convertToOrderInfo(updated));
    }

    private OrderInfo convertToOrderInfo(Order order) {
        return mapper.convertValue(order, OrderInfo.class);
    }


    private Inventory topUpInventory(Item item, int qty) {
        return createInventory(item, qty, InventoryType.T);
    }

    private Inventory withdrawalInventory(Item item, int qty) {
        return createInventory(item, qty, InventoryType.W);
    }

    private Inventory createInventory(Item item, int qty, InventoryType type) {
        return Inventory.builder()
                .item(item)
                .quantity(qty)
                .type(type)
                .build();
    }
}
