package com.sg.obs.dto.item;

import com.sg.obs.dto.BaseInfo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ItemInfo extends BaseInfo {

    private Long id;
    private String name;
    private Double price;
    private Integer remainingStock;
}
