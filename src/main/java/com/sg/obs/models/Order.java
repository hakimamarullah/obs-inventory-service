package com.sg.obs.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Optional;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    @JsonBackReference
    private Item item;

    @Column(name = "QTY", nullable = false)
    @Comment(on = "QTY", value = "Order quantity")
    private Integer qty;


    @Column(name = "PRICE", nullable = false)
    @Comment(on = "PRICE", value = "Grand total for current item''s price. Assuming price on item can be changed")
    private Double price;

    public Long getItemId() {
        return Optional.ofNullable(item).map(Item::getId).orElse(null);
    }

    public String getItemName() {
        return Optional.ofNullable(item).map(Item::getName).orElse(null);
    }

}
