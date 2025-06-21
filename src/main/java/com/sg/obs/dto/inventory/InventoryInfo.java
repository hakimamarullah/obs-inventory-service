package com.sg.obs.dto.inventory;

import com.sg.obs.enums.InventoryType;
import com.sg.obs.dto.BaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryInfo extends BaseInfo {

    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private InventoryType type;
}

