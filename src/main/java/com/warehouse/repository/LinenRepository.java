package com.warehouse.repository;

import com.warehouse.entity.LinenItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinenRepository extends JpaRepository<LinenItem, Long> {
    LinenItem findByItemId(String itemId);
    Page<LinenItem> findByStatus(LinenItem.LinenStatus status, Pageable pageable);
    Page<LinenItem> findAll(Specification<LinenItem> linenItemSpecification, Pageable pageable);
}