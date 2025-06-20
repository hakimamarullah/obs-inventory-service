package com.sg.obs.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sg.obs.enums.InventoryType;
import com.sg.obs.models.converter.InventoryTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "INVENTORY")
@Getter
@Setter
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVENTORY_SEQ")
    @SequenceGenerator(name = "INVENTORY_SEQ", sequenceName = "INVENTORY_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    @JsonBackReference
    private Item item;

    @Column(name = "QTY", nullable = false)
    @Comment(on = "QTY", value = "Inventory quantity")
    private Integer quantity;

    @Convert(converter = InventoryTypeConverter.class)
    @Column(name = "TYPE", nullable = false, length = 1)
    @Comment(on = "TYPE", value = "Inventory type. T = Top-Up, W = Withdrawal")
    private InventoryType type;
}
