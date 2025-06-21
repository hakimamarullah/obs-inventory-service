package com.sg.obs.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequest {

    @NotNull(message = "Order no is required")
    private String orderNo;

    @NotNull(message = "Order quantity is required")
    @Min(value = 0, message = "Item quantity must be greater than 0")
    private Integer qty;

    private Double price;

    private Long itemId;
}

