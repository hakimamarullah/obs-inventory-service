package com.sg.obs.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateItemRequest extends CreateItemRequest {

    @NotNull(message = "Item id is required")
    private Long id;
}
