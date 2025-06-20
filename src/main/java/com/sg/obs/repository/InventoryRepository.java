package com.sg.obs.repository;

import com.sg.obs.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    int removeById(Long id);
}
