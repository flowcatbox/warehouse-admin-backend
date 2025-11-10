package com.warehouse.repository;

import com.warehouse.entity.LinenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinenRepository extends JpaRepository<LinenItem, Long> {
    LinenItem findByItemId(String itemId);
}