package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.transaction.annotation.Transactional;

public interface InventoryService {


    // a. Get
    @Transactional(readOnly = true)
    ApiResponse<InventoryInfo> getInventoryById(Long id);

    // b. Listing with pagination
    @Transactional(readOnly = true)
    ApiResponse<PagedModel<InventoryInfo>> getInventoryList(Pageable pageable);

    // c. Save (create)
    @Transactional
    ApiResponse<InventoryInfo> addInventory(CreateInventoryRequest payload);

    // d. Edit (update)
    @Transactional
    ApiResponse<InventoryInfo> updateInventory(UpdateInventoryRequest payload);

    // e. Delete
    @Transactional
    ApiResponse<String> deleteInventoryById(Long id);
}
