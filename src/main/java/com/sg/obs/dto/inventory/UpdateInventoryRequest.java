package com.sg.obs.dto.inventory;

import com.sg.obs.enums.InventoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryRequest implements Serializable {

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

