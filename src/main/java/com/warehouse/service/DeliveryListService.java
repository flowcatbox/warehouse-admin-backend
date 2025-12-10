package com.warehouse.service;

import com.warehouse.entity.DeliveryList;
import com.warehouse.repository.DeliveryListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryListService {
    private final DeliveryListRepository deliveryListRepository;

    public Page<DeliveryList> findAll(Pageable pageable) {
        return deliveryListRepository.findAll(pageable);
    }

    public Page<DeliveryList> getDeliveryListsPagination(Pageable pageable,
                                                         String deliveryListId,
                                                         String deliveryDateStart,
                                                         String deliveryDateEnd,
                                                         String departmentId) {
        return deliveryListRepository.findAll((Specification<DeliveryList>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(deliveryListId)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("deliveryListId")),
                        "%" + deliveryListId.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(departmentId)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("departmentId")),
                        "%" + departmentId.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(deliveryDateStart)) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), deliveryDateStart));
            }

            if (StringUtils.hasText(deliveryDateEnd)) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), deliveryDateEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public DeliveryList createDeliveryList(DeliveryList deliveryList) {
        return deliveryListRepository.save(deliveryList);
    }

    public DeliveryList updateDeliveryList(Long id, DeliveryList deliveryList) {
        return deliveryListRepository.findById(id)
                .map(dbDeliveryList -> {
                    dbDeliveryList.setDeliveryListId(deliveryList.getDeliveryListId());
                    dbDeliveryList.setDepartmentId(deliveryList.getDepartmentId());
                    dbDeliveryList.setDeliveryDate(deliveryList.getDeliveryDate());
                    dbDeliveryList.setNote(deliveryList.getNote());
                    return deliveryListRepository.save(dbDeliveryList);
                }).orElse(null);
    }

    public boolean deleteDeliveryList(Long id) {
        if (deliveryListRepository.findById(id).isPresent()) {
            deliveryListRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}