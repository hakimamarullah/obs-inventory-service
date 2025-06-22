package com.sg.obs.dto.item;

import com.sg.obs.dto.BaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemInfo extends BaseInfo implements Serializable {

    private Long id;
    private String name;
    private Double price;
    private Integer remainingStock;
}
