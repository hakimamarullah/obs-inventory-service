package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    ApiResponse<PageWrapper<ItemInfo>> getItemsList(Pageable pageable, String filter);

    ApiResponse<ItemInfo> getItemById(Long id);

    ApiResponse<ItemInfo> addItem(CreateItemRequest payload);

    ApiResponse<String> deleteItemById(Long id);

    ApiResponse<ItemInfo> updateItem(UpdateItemRequest payload);
}
