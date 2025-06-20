package com.sg.obs.repository;

import com.sg.obs.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {


    @Query(value = "SELECT NEXT VALUE FOR ORDER_SEQ", nativeQuery = true)
    Long getNextOrderSeq();

    int removeByOrderNo(String orderNo);
}
