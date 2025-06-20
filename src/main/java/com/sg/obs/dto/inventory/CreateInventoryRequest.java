package com.sg.obs.dto.inventory;

import com.sg.obs.enums.InventoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInventoryRequest {

    @NotNull(message = "Item id is required")
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Type is required (T/W)")
    @Schema(allowableValues = {"T", "W"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private InventoryType type;
}

