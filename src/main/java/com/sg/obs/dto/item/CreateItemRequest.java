package com.sg.obs.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateItemRequest {

    @NotBlank(message = "Item name is required")
    @Length(max = 255, message = "Item name must be less than 255 characters")
    private String name;

    @NotNull(message = "Item price is required")
    @Min(value = 0, message = "Item price must be greater than 0")
    private Double price;
}
