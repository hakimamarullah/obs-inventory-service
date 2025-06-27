package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.config.HazelcastConfig;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import com.sg.obs.exception.DataNotFoundException;
import com.sg.obs.models.Item;
import com.sg.obs.repository.ItemRepository;
import com.sg.obs.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSvc implements ItemService {

    public static final String ITEM_WITH_ID_D_NOT_FOUND = "Item with id %d not found";
    private final ItemRepository itemRepository;

    private final ObjectMapper mapper;


    @Transactional(readOnly = true)
    @Cacheable(value = {HazelcastConfig.PAGED_ITEM_CACHE}, keyGenerator = "pageableKeyGenerator")
    @Override
    public ApiResponse<PageWrapper<ItemInfo>> getItemsList(Pageable pageable, String filter) {
        if (!StringUtils.isBlank(filter)) {
            Page<ItemInfo> items = itemRepository.findByNameContainingIgnoreCase(filter, pageable).map(this::convertToItemInfo);
            return ApiResponse.setSuccess(PageWrapper.of(items));
        }
        Page<ItemInfo> items = itemRepository.findAll(pageable).map(this::convertToItemInfo);
        return ApiResponse.setSuccess(PageWrapper.of(new PagedModel<>(items)));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = {HazelcastConfig.ITEM_CACHE}, key = "#id")
    @Override
    public ApiResponse<ItemInfo> getItemById(Long id) {
        ItemInfo itemInfo = itemRepository.findById(id)
                .map(this::convertToItemInfo)
                .orElseThrow(() -> new DataNotFoundException(String.format(ITEM_WITH_ID_D_NOT_FOUND, id)));
        return ApiResponse.setSuccess(itemInfo);
    }

    @Transactional
    @CacheEvict(value = {HazelcastConfig.PAGED_ITEM_CACHE}, allEntries = true)
    @Override
    public ApiResponse<ItemInfo> addItem(CreateItemRequest payload) {
        Item newItem = itemRepository.save(mapper.convertValue(payload, Item.class));
        return ApiResponse.setResponse(convertToItemInfo(newItem), 201);
    }

    @Transactional
    @Modifying
    @Caching(evict = {
            @CacheEvict(value = {HazelcastConfig.ITEM_CACHE}, key = "#id"),
            @CacheEvict(value = {HazelcastConfig.PAGED_ITEM_CACHE}, allEntries = true)
    })
    @Override
    public ApiResponse<String> deleteItemById(Long id) {
        int count = itemRepository.removeById(id);
        if (count == 0) {
            throw new DataNotFoundException(String.format(ITEM_WITH_ID_D_NOT_FOUND, id));
        }
        var response = new ApiResponse<String>();
        response.setMessage(String.format("Item with id %d deleted successfully", id));
        return response;
    }

    @Transactional
    @Modifying
    @Caching(evict = {
            @CacheEvict(value = {HazelcastConfig.ITEM_CACHE}, key = "#payload.id"),
            @CacheEvict(value = {HazelcastConfig.PAGED_ITEM_CACHE}, allEntries = true)
    })
    @Override
    public ApiResponse<ItemInfo> updateItem(UpdateItemRequest payload) {
        Item item = itemRepository.findById(payload.getId())
                .orElseThrow(() -> new DataNotFoundException(String.format(ITEM_WITH_ID_D_NOT_FOUND, payload.getId())));
        item.setName(payload.getName());
        item.setPrice(payload.getPrice());
        itemRepository.save(item);
        return ApiResponse.setSuccess(convertToItemInfo(item));
    }

    private ItemInfo convertToItemInfo(Item item) {
        ItemInfo itemInfo = mapper.convertValue(item, ItemInfo.class);
        itemInfo.setRemainingStock(item.getRemainingStock());
        return itemInfo;
    }


}
