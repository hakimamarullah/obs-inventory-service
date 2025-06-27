package com.sg.obs.repository;

import com.sg.obs.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    int removeById(Long id);

    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
