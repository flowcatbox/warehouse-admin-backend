package com.warehouse.service;

import com.warehouse.entity.Item;
import com.warehouse.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Page<Item> findAll(Specification<Item> specification, Pageable pageable) {
        return itemRepository.findAll(specification, pageable);
    }

    public Page<Item> getItemsWithPagination(Pageable pageable,
                                       String item_description,
                                       String item_id) {
        return itemRepository.findAll((Specification<Item>) (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(item_description)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("item_description")),
                        "%" + item_description.toLowerCase() + "%"
                ));
            }

            if(StringUtils.hasText(item_id)){
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("item_id")),
                        "%" + item_id + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item itemInfo) {
        return itemRepository.findById(id)
                .map(item -> {
                     item.setItem_description(itemInfo.getItem_description());
                     item.setUnit_of_price(itemInfo.getUnit_of_price());
                     item.setUnit(itemInfo.getUnit());
                     item.setItem_graph(itemInfo.getItem_graph());
                     return itemRepository.save(item);
                 })
                .orElse(null);
    }

    public boolean deleteItem(Long id) {
        if(itemRepository.existsById(id)){
            itemRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }

}
