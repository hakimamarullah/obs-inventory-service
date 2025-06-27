package com.sg.obs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.models.Inventory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Setter(onMethod_ = {@Autowired})
public class InventoryBaseSvc {

    protected ObjectMapper mapper;

    public InventoryInfo toInfo(Inventory inv) {
        InventoryInfo info = mapper.convertValue(inv, InventoryInfo.class);
        if (inv.getItem() != null) {
            info.setItemId(inv.getItem().getId());
            info.setItemName(inv.getItem().getName());
        }
        return info;
    }
}
