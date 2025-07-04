package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.config.MapperConfig;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
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
import com.sg.obs.utility.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderSvcTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private final ObjectMapper mapper = new MapperConfig().objectMapper();

    @InjectMocks
    private OrderSvc orderSvc;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderSvc, "mapper", mapper);
    }

    @Test
    void givenOrdersExist_whenGetOrderList_thenReturnPagedModel() {
        // Mock repo to return a paged model of orders
        Order order = new Order();
        order.setOrderNo("20250600000001");

        Item item = new Item();
        item.setName("Item 1");
        order.setItem(new Item());
        order.setQty(1);
        order.setPrice(1.0);

        Page<Order> ordersPage = new PageImpl<>(Collections.singletonList(order));
        doReturn(ordersPage).when(orderRepository).findAll(any(Pageable.class));

        // Act
        ApiResponse<PageWrapper<OrderInfo>> response = orderSvc.getOrderList(PageRequest.ofSize(10));

        // Assert
        PageWrapper<OrderInfo> pagedModel = response.getData();

        assertNotNull(pagedModel);
        assertNotNull(pagedModel.getContent());
        assertEquals(1, pagedModel.getContent().size());
        OrderInfo orderInfo = pagedModel.getContent().getFirst();

        assertEquals(order.getOrderNo(), orderInfo.getOrderNo());
        assertEquals(order.getQty(), orderInfo.getQty());
        assertEquals(order.getPrice(), orderInfo.getPrice());
        assertEquals(order.getItemName(), orderInfo.getItemName());

        PageWrapper.PageMetadata metadata = pagedModel.getMetadata();
        assertNotNull(metadata);
        assertEquals(1, metadata.getTotalPages());
        assertEquals(1, metadata.getTotalElements());
        assertEquals(1, metadata.getSize());
        assertEquals(0, metadata.getNumber());


        verify(orderRepository).findAll(any(Pageable.class));
    }

    @Test
    void givenOrderNo_whenGetOrderByOrderNo_thenReturnOrder() {
        // Mock repo to return an order
        Order order = new Order();
        order.setOrderNo("20250600000001");
        order.setQty(1);
        order.setPrice(1.0);

        Item item = new Item();
        item.setName("Item 1");
        order.setItem(new Item());

        doReturn(Optional.of(order)).when(orderRepository).findById(any(String.class));

        // Act
        ApiResponse<OrderInfo> response = orderSvc.getOrderByOrderNo("20250600000001");

        // Assert
        OrderInfo orderInfo = response.getData();

        assertNotNull(orderInfo);
        assertEquals(order.getOrderNo(), orderInfo.getOrderNo());
        assertEquals(order.getQty(), orderInfo.getQty());
        assertEquals(order.getPrice(), orderInfo.getPrice());
        assertEquals(order.getItemName(), orderInfo.getItemName());

        verify(orderRepository).findById(any(String.class));
    }


    @Test
    void givenOrderDoesNotExist_whenGetOrderByOrderNo_thenThrowDataNotFoundException() {
        // Mock repo to return null
        doReturn(Optional.empty()).when(orderRepository).findById(any(String.class));

        // Act & Assert
        try {
            orderSvc.getOrderByOrderNo("20250600000001");
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Order with orderNo 20250600000001 not found", e.getMessage());
        }

        verify(orderRepository).findById(any(String.class));
    }

    @Test
    void givenValidOrderRequest_whenCreateOrder_thenShouldSuccess() {
        // Mock Item
        Item item = new Item();
        item.setName("Item 1");
        item.setId(1L);
        item.setPrice(10.0);

        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        inventory.setType(InventoryType.T);
        item.setInventory(Collections.singletonList(inventory));

        doReturn(Optional.of(item)).when(itemRepository).findById(any(Long.class));

        // Mock Inventory
        doReturn(new Inventory()).when(inventoryRepository).save(any(Inventory.class));

        // Mock repo to return sequence
        doReturn(1L).when(orderRepository).getNextOrderSeq();

        // Mock repo to return order
        Order order = new Order();
        order.setOrderNo(OrderUtil.generateOrderNo(1L));
        order.setQty(1);
        order.setPrice(item.getPrice());
        order.setItem(item);

        doReturn(order).when(orderRepository).save(any(Order.class));

        // Act
        CreateOrderRequest payload = new CreateOrderRequest();
        payload.setItemId(1L);
        payload.setQty(1);
        ApiResponse<OrderInfo> response = orderSvc.createOrder(payload);

        // Assert
        OrderInfo orderInfo = response.getData();

        assertNotNull(orderInfo);
        assertEquals(order.getOrderNo(), orderInfo.getOrderNo());
        assertEquals(order.getQty(), orderInfo.getQty());
        assertEquals(order.getPrice(), orderInfo.getPrice());
        assertEquals(order.getItemName(), orderInfo.getItemName());

        verify(itemRepository).findById(any(Long.class));

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());

        Inventory savedInventory = inventoryCaptor.getValue();
        assertEquals(payload.getQty(), savedInventory.getQuantity());
        assertEquals(InventoryType.W, savedInventory.getType());

        verify(orderRepository).save(any(Order.class));
        verify(orderRepository).getNextOrderSeq();
    }

    @Test
    void givenItemDoesNotExist_whenCreateOrder_thenThrowDataNotFoundException() {
        // Mock repo to return empty
        doReturn(Optional.empty()).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            var payload = new CreateOrderRequest();
            payload.setItemId(1L);
            payload.setQty(1);
            orderSvc.createOrder(payload);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Failed to create order. Item not found", e.getMessage());
        }
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenItemInventoryIsEmpty_whenCreateOrder_thenThrowDataNotFoundException() {
        // Mock repo to return dummy item
        doReturn(Optional.of(new Item())).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            var payload = new CreateOrderRequest();
            payload.setItemId(1L);
            payload.setQty(1);
            orderSvc.createOrder(payload);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Failed to create order. Item inventory not found", e.getMessage());
        }
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenItemInventoryNotEnough_whenCreateOrder_thenThrowDataNotFoundException() {
        // Mock repo to return dummy item
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        Inventory inventory = new Inventory();
        inventory.setType(InventoryType.T);
        inventory.setQuantity(15);

        Inventory withdrawal = new Inventory();
        withdrawal.setType(InventoryType.W);
        withdrawal.setQuantity(8);
        item.setInventory(List.of(inventory, withdrawal));
        doReturn(Optional.of(item)).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            var payload = new CreateOrderRequest();
            payload.setItemId(1L);
            payload.setQty(11);
            orderSvc.createOrder(payload);
            fail("Expected InsufficientStockException to be thrown");
        } catch (InsufficientStockException e) {
            assertEquals("Insufficient stock", e.getMessage());
        }
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenOrderNo_whenDeleteByOrderNo_thenShouldSuccess() {
        // Mock repo
        doReturn(1).when(orderRepository).removeByOrderNo(any(String.class));

        // Act
        ApiResponse<String> response = orderSvc.deleteOrderByOrderNo("20250600000001");

        // Assert
        assertEquals("Order with orderNo 20250600000001 deleted successfully", response.getMessage());
        verify(orderRepository).removeByOrderNo(any(String.class));
    }

    @Test
    void givenOrderNo_whenDeleteByOrderNo_thenShouldThrowDataNotFoundException() {
        // Mock repo
        doReturn(0).when(orderRepository).removeByOrderNo(any(String.class));

        // Act & Assert
        try {
            orderSvc.deleteOrderByOrderNo("20250600000001");
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Order with orderNo 20250600000001 not found", e.getMessage());
        }
        verify(orderRepository).removeByOrderNo(any(String.class));
    }

    @Test
    void givenOrderNo_whenUpdateOrder_thenShouldSuccess() {
        // Mock order repo
        Order existingOrder = new Order();

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        inventory.setType(InventoryType.T);
        item.setInventory(List.of(inventory));

        existingOrder.setOrderNo("20250600000001");
        existingOrder.setItem(item);
        existingOrder.setQty(1);
        existingOrder.setPrice(item.getPrice());
        doReturn(Optional.of(existingOrder)).when(orderRepository).findById(any(String.class));

        // Mock order repo
        UpdateOrderRequest payload = new UpdateOrderRequest();
        payload.setQty(2);
        payload.setOrderNo("20250600000001");
        payload.setPrice(200.0);

        Order updatedOrder = new Order();
        updatedOrder.setOrderNo("20250600000001");
        updatedOrder.setItem(item);
        updatedOrder.setQty(2);
        updatedOrder.setPrice(payload.getPrice());
        doReturn(updatedOrder).when(orderRepository).save(any(Order.class));

        // Act
        ApiResponse<OrderInfo> response = orderSvc.updateOrder(payload);

        // Assert
        OrderInfo actualOrderInfo = response.getData();
        assertEquals("20250600000001", actualOrderInfo.getOrderNo());
        assertEquals(2, actualOrderInfo.getQty());
        assertEquals(200.0, actualOrderInfo.getPrice());

        verify(orderRepository).findById(any(String.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenOrderNoAndItemId_whenUpdateOrder_thenShouldSuccess() {
        // Mock order repo
        Order existingOrder = new Order();

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        inventory.setType(InventoryType.T);
        item.setInventory(List.of(inventory));

        existingOrder.setOrderNo("20250600000001");
        existingOrder.setItem(item);
        existingOrder.setQty(1);
        existingOrder.setPrice(item.getPrice());
        doReturn(Optional.of(existingOrder)).when(orderRepository).findById(any(String.class));

        // Mock item repo
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setPrice(200.0);
        item2.setInventory(List.of(inventory));
        doReturn(Optional.of(item2)).when(itemRepository).findById(any(Long.class));

        // Mock inventory repo
        doReturn(new ArrayList<>()).when(inventoryRepository).saveAll(anyList());

        // Mock order repo
        UpdateOrderRequest payload = new UpdateOrderRequest();
        payload.setQty(1);
        payload.setOrderNo("20250600000001");
        payload.setItemId(2L);

        Order updatedOrder = new Order();
        updatedOrder.setOrderNo("20250600000001");
        updatedOrder.setItem(item2);
        updatedOrder.setQty(1);
        updatedOrder.setPrice(item2.getPrice());
        doReturn(updatedOrder).when(orderRepository).save(any(Order.class));

        // Act
        ApiResponse<OrderInfo> response = orderSvc.updateOrder(payload);

        // Assert
        OrderInfo actualOrderInfo = response.getData();
        assertEquals("20250600000001", actualOrderInfo.getOrderNo());
        assertEquals(1, actualOrderInfo.getQty());
        assertEquals(200.0, actualOrderInfo.getPrice());

        ArgumentCaptor<List<Inventory>> inventoryCaptor = ArgumentCaptor.forClass(List.class);

        verify(inventoryRepository).saveAll(inventoryCaptor.capture());

        List<Inventory> savedInventories = inventoryCaptor.getValue();
        assertEquals(2, savedInventories.size());
        assertEquals(InventoryType.T, savedInventories.getFirst().getType());
        assertEquals(1, savedInventories.get(0).getQuantity());
        assertEquals(item.hashCode(), savedInventories.get(0).getItem().hashCode());

        assertEquals(InventoryType.W, savedInventories.get(1).getType());
        assertEquals(1, savedInventories.get(1).getQuantity());
        assertEquals(item2.hashCode(), savedInventories.get(1).getItem().hashCode());

        verify(itemRepository).findById(any(Long.class));
        verify(orderRepository).findById(any(String.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void givenOrderNoAndQty_whenUpdateOrder_thenShouldThrowInsufficientStockException() {
        // Mock order repo
        Order existingOrder = new Order();

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        inventory.setType(InventoryType.T);
        item.setInventory(List.of(inventory));

        existingOrder.setOrderNo("20250600000001");
        existingOrder.setItem(item);
        existingOrder.setQty(1);
        existingOrder.setPrice(item.getPrice());
        doReturn(Optional.of(existingOrder)).when(orderRepository).findById(any(String.class));

        // Mock item repo
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setPrice(200.0);
        item2.setInventory(List.of(inventory));
        doReturn(Optional.of(item2)).when(itemRepository).findById(any(Long.class));

        // Mock order repo
        UpdateOrderRequest payload = new UpdateOrderRequest();
        payload.setQty(12);
        payload.setOrderNo("20250600000001");
        payload.setItemId(2L);

        // Act
        try {
            orderSvc.updateOrder(payload);
            fail("Expected InsufficientStockException to be thrown");
        } catch (InsufficientStockException e) {
            assertEquals("Insufficient stock", e.getMessage());
        }

        // Assert
        verify(itemRepository).findById(any(Long.class));
        verify(orderRepository).findById(any(String.class));
    }

    @Test
    void givenOrderNoAndItemIdNull_whenUpdateOrder_thenShouldThrowInsufficientStockException() {
        // Mock order repo
        Order existingOrder = new Order();

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        inventory.setType(InventoryType.T);
        item.setInventory(List.of(inventory));

        existingOrder.setOrderNo("20250600000001");
        existingOrder.setItem(item);
        existingOrder.setQty(1);
        existingOrder.setPrice(item.getPrice());
        doReturn(Optional.of(existingOrder)).when(orderRepository).findById(any(String.class));


        // Mock order repo
        UpdateOrderRequest payload = new UpdateOrderRequest();
        payload.setQty(1);
        payload.setOrderNo("20250600000001");

        doReturn(existingOrder).when(orderRepository).save(any(Order.class));

        // Act
        ApiResponse<OrderInfo> response = orderSvc.updateOrder(payload);

        // Assert
        OrderInfo actualOrderInfo = response.getData();
        assertEquals("20250600000001", actualOrderInfo.getOrderNo());
        assertEquals(1, actualOrderInfo.getQty());
        assertEquals(100.0, actualOrderInfo.getPrice());

        verify(orderRepository).findById(any(String.class));
        verify(orderRepository).save(any(Order.class));
    }
}
