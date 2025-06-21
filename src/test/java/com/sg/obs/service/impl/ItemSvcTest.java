package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.config.MapperConfig;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import com.sg.obs.enums.InventoryType;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.models.Inventory;
import com.sg.obs.models.Item;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemSvcTest {

    @Mock
    private ItemRepository itemRepository;


    private final ObjectMapper mapper = new MapperConfig().objectMapper();

    @InjectMocks
    private ItemSvc itemSvc;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(itemSvc, "mapper", mapper);
    }

    @Test
    void givenItemsExist_whenGetItemsList_thenReturnPagedModel() {
        // Mock the repository to return some items
        Item item = new Item();
        item.setName("Item 1");
        item.setId(1L);
        item.setPrice(10.0);
        Inventory inventory = new Inventory();
        inventory.setType(InventoryType.T);
        inventory.setQuantity(10);
        item.setInventory(Collections.singletonList(inventory));

        Page<Item> itemsPage = new PageImpl<>(Collections.singletonList(item));
        doReturn(itemsPage).when(itemRepository).findAll(any(Pageable.class));

        // Act
        ApiResponse<PagedModel<ItemInfo>> response = itemSvc.getItemsList(Pageable.ofSize(10));

        // Assert
        PagedModel<ItemInfo> pagedModel = response.getData();

        assertNotNull(pagedModel);
        assertNotNull(pagedModel.getContent());
        assertEquals(1, pagedModel.getContent().size());
        ItemInfo itemInfo = pagedModel.getContent().getFirst();

        assertEquals(item.getName(), itemInfo.getName());
        assertEquals(item.getId(), itemInfo.getId());
        assertEquals(item.getPrice(), itemInfo.getPrice());
        assertEquals(item.getRemainingStock(), itemInfo.getRemainingStock());

        PagedModel.PageMetadata metadata = pagedModel.getMetadata();
        assertNotNull(metadata);
        assertEquals(1, metadata.totalPages());
        assertEquals(1, metadata.totalElements());
        assertEquals(1, metadata.size());
        assertEquals(0, metadata.number());

        verify(itemRepository).findAll(any(Pageable.class));

    }

    @Test
    void givenItemId_whenGetItemById_thenReturnItem() {
        // Mock the repository to return an item
        Item item = new Item();
        item.setName("Item 1");
        item.setId(1L);
        item.setPrice(10.0);
        Inventory inventory = new Inventory();
        inventory.setType(InventoryType.T);
        inventory.setQuantity(10);
        item.setInventory(Collections.singletonList(inventory));

        doReturn(Optional.of(item)).when(itemRepository).findById(any(Long.class));

        // Act
        ApiResponse<ItemInfo> response = itemSvc.getItemById(1L);

        // Assert
        ItemInfo itemInfo = response.getData();

        assertNotNull(itemInfo);
        assertEquals(item.getName(), itemInfo.getName());
        assertEquals(item.getId(), itemInfo.getId());
        assertEquals(item.getPrice(), itemInfo.getPrice());
        assertEquals(item.getRemainingStock(), itemInfo.getRemainingStock());

        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenItemDoesNotExist_whenGetItemById_thenThrowDataNotFoundException() {
        // Mock the repository to return null
        doReturn(Optional.empty()).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            itemSvc.getItemById(1L);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Item with id 1 not found", e.getMessage());
        }

        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenValidItemPayload_whenAddItem_thenReturnItemInfo() {
        // Create a valid item payload
        CreateItemRequest payload = new CreateItemRequest();
        payload.setName("Item 1");
        payload.setPrice(10.0);

        // Mock the repository to return the saved item
        doReturn(mapper.convertValue(payload, Item.class)).when(itemRepository).save(any(Item.class));

        // Act
        ApiResponse<ItemInfo> response = itemSvc.addItem(payload);

        // Assert
        ItemInfo itemInfo = response.getData();

        assertNotNull(itemInfo);
        assertEquals(payload.getName(), itemInfo.getName());
        assertEquals(payload.getPrice(), itemInfo.getPrice());
        assertEquals(0, itemInfo.getRemainingStock());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void givenItemId_whenDeleteItemById_thenDeleteItem() {
        // Mock repo
        doReturn(1).when(itemRepository).removeById(any(Long.class));

        // Act
        ApiResponse<String> response = itemSvc.deleteItemById(1L);

        // Assert
        assertEquals("Item with id 1 deleted successfully", response.getMessage());
        verify(itemRepository).removeById(any(Long.class));
    }

    @Test
    void givenItemId_whenDeleteItemById_thenThrowDataNotFoundException() {
        // Mock repo
        doReturn(0).when(itemRepository).removeById(any(Long.class));

        // Act & Assert
        try {
            itemSvc.deleteItemById(1L);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Item with id 1 not found", e.getMessage());
        }
        verify(itemRepository).removeById(any(Long.class));
    }

    @Test
    void givenValidItemPayload_whenUpdateItem_thenReturnItemInfo() {
        // Create a valid item payload
        UpdateItemRequest payload = new UpdateItemRequest();
        payload.setId(1L);
        payload.setName("Item 1");
        payload.setPrice(10.0);

        // Mock the repository to return the saved item
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Item Existing");
        existingItem.setPrice(12.0);
        doReturn(Optional.of(existingItem)).when(itemRepository).findById(any(Long.class));

        // Act
        ApiResponse<ItemInfo> response = itemSvc.updateItem(payload);

        // Assert
        ItemInfo itemInfo = response.getData();

        assertNotNull(itemInfo);
        assertEquals(payload.getName(), itemInfo.getName());
        assertEquals(payload.getPrice(), itemInfo.getPrice());
        assertEquals(0, itemInfo.getRemainingStock());

        verify(itemRepository).findById(any(Long.class));
    }

    @Test
    void givenItemDoesNotExist_whenUpdateItem_thenThrowDataNotFoundException() {
        // Mock repo
        doReturn(Optional.empty()).when(itemRepository).findById(any(Long.class));

        // Act & Assert
        try {
            var request = new UpdateItemRequest();
            request.setId(1L);
            itemSvc.updateItem(request);
            fail("Expected DataNotFoundException to be thrown");
        } catch (DataNotFoundException e) {
            assertEquals("Item with id 1 not found", e.getMessage());
        }
        verify(itemRepository).findById(any(Long.class));
    }
}