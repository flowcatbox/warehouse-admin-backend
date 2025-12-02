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
import java.util.Date;
import java.util.List;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryListService {
    private final DeliveryListRepository deliveryListRepository;

    public Page<DeliveryList> findAll(Pageable pageable) {
        return deliveryListRepository.findAll(pageable);
    }

    public Page<DeliveryList> getDeliveryListsPagination(Pageable pageable,
                                                         String delivery_list_id,
                                                         String department_id,
                                                         String delivery_date_start,
                                                         String delivery_date_end){
        return deliveryListRepository.findAll((Specification<DeliveryList>) (root, query, criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(delivery_list_id)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("delivery_list_id")),
                        "%" + delivery_list_id.toLowerCase() + "%"
                ));
            }

            if(StringUtils.hasText(department_id)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("department_id")),
                        "%" + department_id.toLowerCase() + "%"
                ));
            }

            if(StringUtils.hasText(delivery_date_start)){
                LocalDateTime localDateTime = LocalDateTime.parse(delivery_date_start + "T00:00:00");
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("delivery_date_start"), localDateTime));
            }

            if(StringUtils.hasText(delivery_date_end)){
                LocalDateTime localDateTime = LocalDateTime.parse(delivery_date_end + "T23:59:59");
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("delivery_date_end"), localDateTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public DeliveryList createDeliveryList(DeliveryList deliveryList){
        //update delivery list date
        return deliveryListRepository.save(deliveryList);
    }

    public DeliveryList updateDeliveryList(Long id, DeliveryList deliveryList){
        return deliveryListRepository.findById(id).
                map(dbDeliveryList -> {
                    dbDeliveryList.setDelivery_list_id(deliveryList.getDelivery_list_id());
                    dbDeliveryList.setDepartment_id(deliveryList.getDepartment_id());
                    dbDeliveryList.setDelivery_date(deliveryList.getDelivery_date());
                    dbDeliveryList.setNote(deliveryList.getNote());
                    return deliveryListRepository.save(dbDeliveryList);
                }).orElse(null);
    }

    public boolean deleteDeliveryList(Long id){
        if(deliveryListRepository.findById(id).isPresent()){
            deliveryListRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}
