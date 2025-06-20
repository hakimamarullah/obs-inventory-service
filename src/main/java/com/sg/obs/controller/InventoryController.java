package com.sg.obs.controller;

import com.sg.obs.annotations.LogRequestResponse;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import com.sg.obs.service.InventoryService;
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
@RequestMapping("/v1/inventories")
@RequiredArgsConstructor
@LogRequestResponse
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get inventory by ID")
    public ApiResponse<InventoryInfo> getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all inventories with pagination")
    public ApiResponse<PagedModel<InventoryInfo>> getInventoryList(@ParameterObject Pageable pageable) {
        return inventoryService.getInventoryList(pageable);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new inventory record (Top-Up or Withdrawal)")
    public ApiResponse<InventoryInfo> addInventory(@RequestBody @Valid CreateInventoryRequest payload) {
        return inventoryService.addInventory(payload);
    }


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an existing inventory record")
    public ApiResponse<InventoryInfo> updateInventory(@RequestBody @Valid UpdateInventoryRequest payload) {
        return inventoryService.updateInventory(payload);
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete inventory by ID")
    public ApiResponse<String> deleteInventory(@PathVariable Long id) {
        return inventoryService.deleteInventoryById(id);
    }
}

