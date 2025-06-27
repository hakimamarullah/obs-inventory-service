package com.sg.obs.repository;

import com.sg.obs.dto.inventory.InventorySummary;
import com.sg.obs.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    int removeById(Long id);

    @Query("""
            SELECT\s
                i.item.name AS itemName,
                i.item.id AS itemId,
                SUM(CASE WHEN i.type = 'W' THEN i.quantity ELSE 0 END) AS totalWithdraw,
                SUM(CASE WHEN i.type = 'T' THEN i.quantity ELSE 0 END) AS totalTopUp,
                COUNT(CASE WHEN i.type = 'T' THEN 1 ELSE NULL END) AS topUpCount,
                COUNT(CASE WHEN i.type = 'W' THEN 1 ELSE NULL END) AS withdrawCount
            FROM Inventory i
            WHERE i.item.id = :itemId
            GROUP BY i.item.id, i.item.name
           \s""")
    Optional<InventorySummary> getInventorySummaryByItemId(Long itemId);

    Page<Inventory> findByItemId(Long itemId, Pageable pageable);
}
