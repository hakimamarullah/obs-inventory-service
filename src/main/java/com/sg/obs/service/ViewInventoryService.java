package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.InventorySummary;
import org.springframework.data.domain.Pageable;

public interface ViewInventoryService {

    ApiResponse<InventorySummary> getInventorySummaryByItemId(Long id);

    ApiResponse<PageWrapper<InventoryInfo>> getInventoryListByItemId(Long id, Pageable pageable);
}
