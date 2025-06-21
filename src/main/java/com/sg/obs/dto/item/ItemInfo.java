package com.sg.obs.dto.item;

import com.sg.obs.dto.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ItemInfo extends BaseInfo {

    private Long id;
    private String name;
    private Double price;
    private Integer remainingStock;
}
