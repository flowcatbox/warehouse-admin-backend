package com.warehouse.service;

import org.springframework.util.StringUtils;
import com.warehouse.entity.LinenItem;
import com.warehouse.repository.LinenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinenService {
    private final LinenRepository linenRepository;

    public Page<LinenItem> getAllLinenItems(Pageable pageable) {
        return linenRepository.findAll(pageable);
    }

    public Page<LinenItem> getLinenItemsWithPagination(Pageable pageable,
                                                       String category,
                                                       String description,
                                                       String itemId,
                                                       String location,
                                                       String status,
                                                       String createdTimeStart,
                                                       String createdTimeEnd){
        return linenRepository.findAll((Specification<LinenItem>) (root, query, criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(category)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("category")),
                        "%" + category.toLowerCase() + "%"
                ));
            }

            if(StringUtils.hasText(description)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                ));
            }

            if(StringUtils.hasText(itemId)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("itemId")),
                        "%" + itemId.toLowerCase() + "%")
                );
            }

            if(StringUtils.hasText(location)){
                predicates.add(criteriaBuilder.equal(root.get("location"), location));
            }

            if(StringUtils.hasText(status)){
                LinenItem.LinenStatus linenStatus = LinenItem.LinenStatus.valueOf(status.toUpperCase());
                predicates.add(criteriaBuilder.equal(root.get("status"), linenStatus));
            }

            if(StringUtils.hasText(createdTimeStart)){
                LocalDateTime start = LocalDateTime.parse(createdTimeStart + "T00:00:00");
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start));
            }

            if(StringUtils.hasText(createdTimeEnd)){
                LocalDateTime end = LocalDateTime.parse(createdTimeEnd + "T23:59:59");
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public Optional<LinenItem> getLinenById(Long id){
        return linenRepository.findById(id);
    }

    public LinenItem createLinen(LinenItem linenItemInfo){
        linenItemInfo.setCreatedAt(LocalDateTime.now());
        return linenRepository.save(linenItemInfo);
    }

    public boolean deleteLinen(Long id) {
        if(linenRepository.findById(id).isPresent()) {
            linenRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
