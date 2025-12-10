package com.warehouse.repository;

import com.warehouse.entity.DeliveryList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryListRepository extends JpaRepository<DeliveryList, Long> {
    Page<DeliveryList> findAll(Specification<DeliveryList> spec, Pageable pageable);
    DeliveryList findByDeliveryListId(String deliveryListId);
}
