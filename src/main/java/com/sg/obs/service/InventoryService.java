package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface InventoryService {




    ApiResponse<InventoryInfo> getInventoryById(Long id);


    ApiResponse<PagedModel<InventoryInfo>> getInventoryList(Pageable pageable);



    ApiResponse<InventoryInfo> addInventory(CreateInventoryRequest payload);



    ApiResponse<InventoryInfo> updateInventory(UpdateInventoryRequest payload);



    ApiResponse<String> deleteInventoryById(Long id);
}
