package com.sg.obs.controller;

import com.sg.obs.annotations.LogRequestResponse;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import com.sg.obs.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
@LogRequestResponse
public class ItemController {

    private final ItemService itemService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get All items with pagination")
    public ApiResponse<PagedModel<ItemInfo>> getItems(@ParameterObject Pageable pageable) {
        return itemService.getItemsList(pageable);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get item by id")
    public ApiResponse<ItemInfo> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new item")
    public ApiResponse<ItemInfo> addItem(@RequestBody @Valid CreateItemRequest payload) {
        return itemService.addItem(payload);
    }


    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update item")
    public ApiResponse<ItemInfo> updateItem(@RequestBody @Valid UpdateItemRequest payload) {
        return itemService.updateItem(payload);
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete item by id")
    public ApiResponse<String> deleteItem(@PathVariable Long id) {
        return itemService.deleteItemById(id);
    }
}
