package com.sg.obs.service;

import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface ItemService {

    ApiResponse<PagedModel<ItemInfo>> getItemsList(Pageable pageable);

    ApiResponse<ItemInfo> getItemById(Long id);

    ApiResponse<ItemInfo> addItem(CreateItemRequest payload);

    ApiResponse<String> deleteItemById(Long id);

    ApiResponse<ItemInfo> updateItem(UpdateItemRequest payload);
}
