package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.models.Inventory;
import com.sg.obs.models.Item;
import com.sg.obs.repository.InventoryRepository;
import com.sg.obs.repository.ItemRepository;
import com.sg.obs.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventorySvc implements InventoryService {

    private static final String NOT_FOUND = "Inventory with id %d not found";

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final ObjectMapper mapper;


    @Transactional(readOnly = true)
    @Override
    public ApiResponse<InventoryInfo> getInventoryById(Long id) {
        InventoryInfo info = inventoryRepository.findById(id)
                .map(this::toInfo)
                .orElseThrow(() -> new DataNotFoundException(String.format(NOT_FOUND, id)));

        return ApiResponse.setSuccess(info);
    }


    @Transactional(readOnly = true)
    @Override
    public ApiResponse<PagedModel<InventoryInfo>> getInventoryList(Pageable pageable) {
        Page<InventoryInfo> page = inventoryRepository.findAll(pageable).map(this::toInfo);
        return ApiResponse.setSuccess(new PagedModel<>(page));
    }


    @Transactional
    @Override
    public ApiResponse<InventoryInfo> addInventory(CreateInventoryRequest payload) {
        Item item = itemRepository.findById(payload.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item not found"));

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQuantity(payload.getQuantity());
        inventory.setType(payload.getType());

        return ApiResponse.setSuccess(toInfo(inventoryRepository.save(inventory)));
    }


    @Transactional
    @Override
    public ApiResponse<InventoryInfo> updateInventory(UpdateInventoryRequest payload) {
        Inventory inventory = inventoryRepository.findById(payload.getId())
                .orElseThrow(() -> new DataNotFoundException(String.format(NOT_FOUND, payload.getId())));

        if (payload.getItemId() != null) {
            Item item = itemRepository.findById(payload.getItemId())
                    .orElseThrow(() -> new DataNotFoundException("Item not found"));
            inventory.setItem(item);
        }
        inventory.setQuantity(payload.getQuantity());
        inventory.setType(payload.getType());

        return ApiResponse.setSuccess(toInfo(inventoryRepository.save(inventory)));
    }


    @Transactional
    @Modifying
    @Override
    public ApiResponse<String> deleteInventoryById(Long id) {
        int count = inventoryRepository.removeById(id);
        if (count == 0) {
            throw new DataNotFoundException(String.format(NOT_FOUND, id));
        }
        var res = new ApiResponse<String>();
        res.setMessage(String.format("Inventory with id %d deleted successfully", id));
        return res;
    }

    private InventoryInfo toInfo(Inventory inv) {
        InventoryInfo info = mapper.convertValue(inv, InventoryInfo.class);
        if (inv.getItem() != null) {
            info.setItemId(inv.getItem().getId());
            info.setItemName(inv.getItem().getName());
        }
        return info;
    }
}

