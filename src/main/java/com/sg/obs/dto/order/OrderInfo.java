package com.sg.obs.dto.order;

import com.sg.obs.dto.BaseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class OrderInfo extends BaseInfo {

    private String orderNo;
    private Long itemId;
    private String itemName;
    private Integer qty;
    private Double price;
}

