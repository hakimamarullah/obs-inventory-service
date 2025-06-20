package com.sg.obs.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderRequest {

    @NotNull(message = "Order no is required")
    private String orderNo;

    @NotNull(message = "Order quantity is required")
    @Min(value = 0, message = "Item quantity must be greater than 0")
    private Integer qty;

    @NotNull(message = "Item price is required")
    @Min(value = 0, message = "Item price must be greater than 0")
    private Double price;

    private Long itemId;
}

