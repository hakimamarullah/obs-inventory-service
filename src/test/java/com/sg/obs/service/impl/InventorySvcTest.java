package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.config.MapperConfig;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import com.sg.obs.enums.InventoryType;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.models.Inventory;
import com.sg.obs.models.Item;
import com.sg.obs.repository.InventoryRepository;
import com.sg.obs.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventorySvcTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    private final ObjectMapper mapper = new MapperConfig().objectMapper();

    @InjectMocks
    private InventorySvc inventorySvc;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(inventorySvc, "mapper", mapper);
    }

    @Test
    void givenInventoryId_whenGetInventoryById_thenSuccess() {
        // Mock inventory repo
        Inventory inventory = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).build();
        doReturn(Optional.of(inventory)).when(inventoryRepository).findById(any(Long.class));

        // Act & Assert
        ApiResponse<InventoryInfo> response = inventorySvc.getInventoryById(1L);
        InventoryInfo actualInfo = response.getData();

        assertEquals(200, response.getCode());
        assertEquals(1L, actualInfo.getId());
        assertEquals(10, actualInfo.getQuantity());
        assertEquals(InventoryType.T, actualInfo.getType());

        verify(inventoryRepository).findById(any(Long.class));
    }

    @Test
    void givenInventoryDoesNotExist_whenGetInventoryById_thenThrowDataNotFoundException() {
        // Mock inventory repo
        doReturn(Optional.empty()).when(inventoryRepository).findById(any(Long.class));

        // Act & Assert
        try {
            inventorySvc.getInventoryById(1L);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Inventory with id 1 not found", e.getMessage());
        }

        verify(inventoryRepository).findById(any(Long.class));
    }

    @Test
    void givenItemExists_whenGetInventoryList_thenSuccess() {
        // Mock the repository to return some items
        Item item = new Item();
        item.setId(1L);
        Inventory inventory = Inventory.builder()
                .id(1L)
                .quantity(10)
                .type(InventoryType.T)
                .item(item)
                .build();

        Page<Inventory> inventoryPage = new PageImpl<>(Collections.singletonList(inventory));
        doReturn(inventoryPage).when(inventoryRepository).findAll(any(Pageable.class));

        // Act
        ApiResponse<PagedModel<InventoryInfo>> response = inventorySvc.getInventoryList(Pageable.ofSize(10));

        // Assert
        assertEquals(200, response.getCode());
        PagedModel<InventoryInfo> pagedModel = response.getData();

        assertNotNull(pagedModel);
        assertNotNull(pagedModel.getContent());
        assertEquals(1, pagedModel.getContent().size());
        InventoryInfo actualInventoryInfo = pagedModel.getContent().getFirst();

        assertEquals(item.getId(), actualInventoryInfo.getItemId());
        assertEquals(inventory.getId(), actualInventoryInfo.getId());
        assertEquals(inventory.getType(), actualInventoryInfo.getType());

        PagedModel.PageMetadata metadata = pagedModel.getMetadata();
        assertNotNull(metadata);
        assertEquals(1, metadata.totalPages());
        assertEquals(1, metadata.totalElements());
        assertEquals(1, metadata.size());
        assertEquals(0, metadata.number());

        verify(inventoryRepository).findAll(any(Pageable.class));
    }

    @Test
    void givenCreateInventoryPayload_whenCreateInventory_thenSuccess() {
        // Prepare params
        CreateInventoryRequest payload = new CreateInventoryRequest();
        payload.setType(InventoryType.T);
        payload.setItemId(1L);
        payload.setQuantity(10);

        // Mock repo to return dummy item
        Item item = new Item();
        item.setId(1L);
        doReturn(Optional.of(item)).when(itemRepository).findById(any(Long.class));

        // Mock save
        Inventory savedInv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).item(item).build();
        doReturn(savedInv).when(inventoryRepository).save(any(Inventory.class));

        // Act
        ApiResponse<InventoryInfo> response = inventorySvc.addInventory(payload);
        InventoryInfo actualInventoryInfo = response.getData();

        // Assert
        assertEquals(201, response.getCode());
        assertEquals(1L, actualInventoryInfo.getItemId());
        assertEquals(10, actualInventoryInfo.getQuantity());
        assertEquals(InventoryType.T, actualInventoryInfo.getType());

        verify(inventoryRepository).save(any(Inventory.class));
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenItemDoesNotExist_whenCreateInventory_thenThrowDataNotFoundException() {
        // Prepare params
        CreateInventoryRequest payload = new CreateInventoryRequest();
        payload.setType(InventoryType.T);
        payload.setItemId(1L);
        payload.setQuantity(10);

        // Mock repo to return dummy item
        doReturn(Optional.empty()).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            inventorySvc.addInventory(payload);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenUpdateInventoryPayload_whenUpdateInventory_thenSuccess() {
        // Prepare params
        UpdateInventoryRequest payload = new UpdateInventoryRequest();
        payload.setId(1L);
        payload.setQuantity(10);
        payload.setItemId(1L);

        // Mock repo to return dummy item
        Item item = new Item();
        item.setId(1L);
        Inventory inv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).item(item).build();
        doReturn(Optional.of(inv)).when(inventoryRepository).findById(any(Long.class));
        doReturn(Optional.of(item)).when(itemRepository).findById(any(Long.class));

        // Mock save
        Inventory savedInv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).item(item).build();
        doReturn(savedInv).when(inventoryRepository).save(any(Inventory.class));

        // Act
        ApiResponse<InventoryInfo> response = inventorySvc.updateInventory(payload);
        InventoryInfo actualInventoryInfo = response.getData();

        // Assert
        assertEquals(200, response.getCode());
        assertEquals(1L, actualInventoryInfo.getItemId());
        assertEquals(10, actualInventoryInfo.getQuantity());
        assertEquals(InventoryType.T, actualInventoryInfo.getType());

        verify(itemRepository).findById(any(Long.class));
        verify(inventoryRepository).findById(any(Long.class));
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void givenItemIdNull_whenUpdateInventory_thenSuccess() {
        // Prepare params
        UpdateInventoryRequest payload = new UpdateInventoryRequest();
        payload.setId(1L);
        payload.setQuantity(10);

        // Mock repo to return dummy item
        Item item = new Item();
        item.setId(1L);
        Inventory inv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).item(item).build();
        doReturn(Optional.of(inv)).when(inventoryRepository).findById(any(Long.class));

        // Mock save
        Inventory savedInv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).item(item).build();
        doReturn(savedInv).when(inventoryRepository).save(any(Inventory.class));

        // Act
        ApiResponse<InventoryInfo> response = inventorySvc.updateInventory(payload);
        InventoryInfo actualInventoryInfo = response.getData();

        // Assert
        assertEquals(200, response.getCode());
        assertEquals(1L, actualInventoryInfo.getItemId());
        assertEquals(10, actualInventoryInfo.getQuantity());
        assertEquals(InventoryType.T, actualInventoryInfo.getType());

        verify(itemRepository, times(0)).findById(any(Long.class));
        verify(inventoryRepository).findById(any(Long.class));
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void givenInventoryDoesNotExist_whenUpdateInventory_thenThrowDataNotFoundException() {
        // Prepare params
        UpdateInventoryRequest payload = new UpdateInventoryRequest();
        payload.setId(1L);
        payload.setQuantity(10);

        // Mock repo to return dummy item
        doReturn(Optional.empty()).when(inventoryRepository).findById(any(Long.class));

        // Act & Assert
        try {
            inventorySvc.updateInventory(payload);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Inventory with id 1 not found", e.getMessage());
        }
        verify(inventoryRepository).findById(any(Long.class));
    }

    @Test
    void givenItemDoesNotExist_whenUpdateInventory_thenThrowDataNotFoundException() {
        // Prepare params
        UpdateInventoryRequest payload = new UpdateInventoryRequest();
        payload.setId(1L);
        payload.setQuantity(10);
        payload.setItemId(1L);

        // Mock repo to return dummy item
        Inventory inv = Inventory.builder().id(1L).quantity(10).type(InventoryType.T).build();
        doReturn(Optional.of(inv)).when(inventoryRepository).findById(any(Long.class));

        // Mock save
        doReturn(Optional.empty()).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            inventorySvc.updateInventory(payload);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Item not found", e.getMessage());
        }
        verify(inventoryRepository).findById(any(Long.class));
        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenInventoryId_whenDeleteInventory_thenSuccess() {
        // Mock repo
        doReturn(1).when(inventoryRepository).removeById(any(Long.class));

        // Act & Assert
        ApiResponse<String> response = inventorySvc.deleteInventoryById(1L);
        assertEquals(200, response.getCode());
        assertEquals("Inventory with id 1 deleted successfully", response.getMessage());
        verify(inventoryRepository).removeById(any(Long.class));
    }

    @Test
    void givenInventoryDoesNotExist_whenDeleteInventory_thenThrowDataNotFoundException() {
        // Mock repo
        doReturn(0).when(inventoryRepository).removeById(any(Long.class));

        // Act & Assert
        try {
            inventorySvc.deleteInventoryById(1L);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Inventory with id 1 not found", e.getMessage());
        }
        verify(inventoryRepository).removeById(any(Long.class));
    }
}