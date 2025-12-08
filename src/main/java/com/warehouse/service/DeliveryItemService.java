package com.warehouse.service;


import com.warehouse.entity.DeliveryItem;
import com.warehouse.repository.DeliveryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryItemService {
    private final DeliveryItemRepository deliveryItemRepository;

    public Page<DeliveryItem> getAllDeliveryItemsByDeliveryId(Pageable pageable, Long deliveryId) {
        return deliveryItemRepository.findByDeliveryId(pageable, deliveryId);
    }

    public Page<DeliveryItem> getAllDeliveryItemByItemId(Pageable pageable, Long itemId) {
        return deliveryItemRepository.findByItemId(pageable, itemId);
    }
}
