package com.sg.obs.dto.inventory;

import com.sg.obs.enums.InventoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateInventoryRequest {

    @NotNull(message = "Inventory id is required")
    private Long id;

    private Long itemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity can't be negative")
    private Integer quantity;

    @NotNull(message = "Type is required (T/W)")
    @Schema(allowableValues = {"T", "W"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private InventoryType type;
}

