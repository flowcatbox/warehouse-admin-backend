// /warehouse_admin_backend_extract_new2/warehouse-admin-backend/src/main/java/com/warehouse/repository/DeliveryItemRepository.java

package com.warehouse.repository;

import com.warehouse.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {

    List<DeliveryItem> findByDeliveryId(Long deliveryId);

    List<DeliveryItem> findByItemId(Long itemId);

    @Query("SELECT SUM(di.quantity * di.price) FROM DeliveryItem di WHERE di.delivery.id = :deliveryId")
    BigDecimal getTotalAmountByDeliveryId(@Param("deliveryId") Long deliveryId);

    void deleteByDeliveryId(Long deliveryId);

    boolean existsByDeliveryIdAndItemId(Long deliveryId, Long itemId);
}