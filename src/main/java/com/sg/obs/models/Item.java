package com.sg.obs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sg.obs.enums.InventoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ITEM")
@Getter
@Setter
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_SEQ")
    @SequenceGenerator(name = "ITEM_SEQ", sequenceName = "ITEM_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NAME")
    @Comment(on = "NAME", value = "Product name")
    private String name;


    @Column(name = "PRICE")
    @Comment(on = "PRICE", value = "Product price")
    private Double price;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item", orphanRemoval = true)
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item", orphanRemoval = true)
    @JsonManagedReference
    private List<Inventory> inventory = new ArrayList<>();

    @JsonIgnore
    public int getRemainingStock() {
        return inventory.parallelStream()
                .map(i -> i.getType() == InventoryType.T ? i.getQuantity() : -i.getQuantity())
                .mapToInt(Integer::intValue)
                .sum();
    }

}
