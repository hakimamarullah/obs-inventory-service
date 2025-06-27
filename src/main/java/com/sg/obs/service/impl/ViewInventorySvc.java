package com.sg.obs.service.impl;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.InventorySummary;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.repository.InventoryRepository;
import com.sg.obs.service.ViewInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewInventorySvc extends InventoryBaseSvc implements ViewInventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public ApiResponse<InventorySummary> getInventorySummaryByItemId(Long id) {
        InventorySummary summary = inventoryRepository.getInventorySummaryByItemId(id)
                .orElseThrow(() -> new DataNotFoundException("No Inventory Found"));
        return ApiResponse.setSuccess(summary);
    }

    @Override
    public ApiResponse<PageWrapper<InventoryInfo>> getInventoryListByItemId(Long id, Pageable pageable) {
        Page<InventoryInfo> page = inventoryRepository.findByItemId(id, pageable).map(this::toInfo);
        return ApiResponse.setSuccess(PageWrapper.of(page));
    }
}
