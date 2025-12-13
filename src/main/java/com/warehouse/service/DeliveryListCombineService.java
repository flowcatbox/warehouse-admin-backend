package com.warehouse.service;

import com.warehouse.dto.DeliveryItemDTO;
import com.warehouse.dto.DeliveryListDTO;
import com.warehouse.dto.DeliveryListResponseDTO;
import com.warehouse.dto.ItemDTO;
import com.warehouse.entity.DeliveryItem;
import com.warehouse.entity.DeliveryList;
import com.warehouse.entity.Department;
import com.warehouse.entity.Item;
import com.warehouse.repository.DepartmentRepository;
import com.warehouse.repository.DeliveryListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryListCombineService {

    private final DeliveryListRepository deliveryListRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * Multi-table join query:
     *  - Primary table: DeliveryList
     *  - Relationships: DeliveryList.items -> DeliveryItem -> Item
     *  - Conditions: departmentCode / deliveryListId / itemId / itemDescription / date range
     *
     * Return: Data for one page packaged into a single DeliveryListResponseDTO,
     * Page<DeliveryListResponseDTO> contains only one element.
     */
    @Transactional(readOnly = true)
    public Page<DeliveryListResponseDTO> getDeliveryList(Pageable pageable,
                                                         String departmentCode,
                                                         String deliveryListId,
                                                         String itemDescription,
                                                         String itemId,
                                                         String deliveryListDateStart,
                                                         String deliveryListDateEnd) {

        // 1. Using Specification to Build Multi-Table Joins + Conditional Filtering
        Page<DeliveryList> deliveryListPage = deliveryListRepository.findAll(
                buildSpecification(departmentCode,
                        deliveryListId,
                        itemDescription,
                        itemId,
                        deliveryListDateStart,
                        deliveryListDateEnd),
                pageable
        );

        if (deliveryListPage.isEmpty()) {
            DeliveryListResponseDTO empty = new DeliveryListResponseDTO();
            empty.setDeliveryList(Collections.emptyList());
            empty.setDeliveryItem(Collections.emptyList());
            empty.setItem(Collections.emptyList());
            empty.setDepartment(Collections.emptyList());
            return new PageImpl<>(Collections.singletonList(empty), pageable, 0);
        }

        // 2. Map DeliveryList -> DeliveryListDTO (each DTO internally carries its own list of DeliveryItemDTOs)
        List<DeliveryListDTO> deliveryListDTOs = new ArrayList<>();
        List<DeliveryItemDTO> allDeliveryItemDTOs = new ArrayList<>();
        Map<Long, ItemDTO> itemDTOMap = new LinkedHashMap<>();
        Set<String> departmentCodes = new LinkedHashSet<>();

        for (DeliveryList deliveryList : deliveryListPage.getContent()) {

            DeliveryListDTO listDTO = new DeliveryListDTO();
            listDTO.setId(deliveryList.getId());
            listDTO.setDeliveryListId(deliveryList.getDeliveryListId());
            listDTO.setDepartmentId(deliveryList.getDepartmentId());
            listDTO.setDeliveryDate(deliveryList.getDeliveryDate());
            listDTO.setNote(deliveryList.getNote());

            if (deliveryList.getDepartmentId() != null) {
                departmentCodes.add(deliveryList.getDepartmentId());
            }

            List<DeliveryItemDTO> itemDTOsForList = new ArrayList<>();

            if (deliveryList.getItems() != null) {
                for (DeliveryItem deliveryItem : deliveryList.getItems()) {

                    DeliveryItemDTO deliveryItemDTO = new DeliveryItemDTO();
                    deliveryItemDTO.setId(deliveryItem.getId());

                    Item item = deliveryItem.getItem();
                    if (item != null) {
                        deliveryItemDTO.setItemID(item.getItemId());
                    }

                    deliveryItemDTO.setDeliveryListId(deliveryList.getDeliveryListId());
                    deliveryItemDTO.setQuantity(deliveryItem.getQuantity());
                    deliveryItemDTO.setPrice(deliveryItem.getPrice());
                    deliveryItemDTO.setNote(deliveryItem.getNote());

                    itemDTOsForList.add(deliveryItemDTO);
                    allDeliveryItemDTOs.add(deliveryItemDTO);

                    if (item != null && !itemDTOMap.containsKey(item.getId())) {
                        ItemDTO itemDTO = new ItemDTO();
                        itemDTO.setId(item.getId());
                        itemDTO.setItemId(item.getItemId());
                        itemDTO.setItemDescription(item.getItemDescription());
                        itemDTO.setUnitOfPrice(item.getUnitOfPrice());
                        itemDTO.setUnit(item.getUnit());
                        itemDTO.setItemGraph(item.getItemGraph());
                        itemDTOMap.put(item.getId(), itemDTO);
                    }
                }
            }

            listDTO.setItems(itemDTOsForList);
            deliveryListDTOs.add(listDTO);
        }

        // 3. Batch query Department information to avoid N+1 queries
        List<Department> departments;
        if (departmentCodes.isEmpty()) {
            departments = Collections.emptyList();
        } else {
            departments = departmentRepository.findByDepartmentCodeIn(
                    departmentCodes.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        }

        // 4. Assemble the final ResponseDTO
        DeliveryListResponseDTO responseDTO = new DeliveryListResponseDTO();
        responseDTO.setDeliveryList(deliveryListDTOs);
        responseDTO.setDeliveryItem(allDeliveryItemDTOs);
        responseDTO.setItem(new ArrayList<>(itemDTOMap.values()));
        responseDTO.setDepartment(departments);

        // This Page contains only one element, packaging all the data on this page into a single DTO for return.
        return new PageImpl<>(
                Collections.singletonList(responseDTO),
                pageable,
                deliveryListPage.getTotalElements()
        );
    }

    /**
     * Construct Specification<DeliveryList>:
     *  - Basic conditions: deliveryListId / departmentCode / date range
     *  - Additional conditions: if itemId / itemDescription exists, join DeliveryItem & Item for filtering
     */
    private Specification<DeliveryList> buildSpecification(String departmentCode,
                                                           String deliveryListId,
                                                           String itemDescription,
                                                           String itemId,
                                                           String deliveryListDateStart,
                                                           String deliveryListDateEnd) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(deliveryListId)) {
                predicates.add(cb.like(
                        cb.lower(root.get("deliveryListId")),
                        "%" + deliveryListId.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(departmentCode)) {
                predicates.add(cb.like(
                        cb.lower(root.get("departmentId")),
                        "%" + departmentCode.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(deliveryListDateStart)) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("deliveryDate"),
                        deliveryListDateStart
                ));
            }

            if (StringUtils.hasText(deliveryListDateEnd)) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("deliveryDate"),
                        deliveryListDateEnd
                ));
            }

            boolean filterByItem = StringUtils.hasText(itemId) || StringUtils.hasText(itemDescription);

            if (filterByItem) {
                // DeliveryList -> DeliveryItem
                Join<DeliveryList, DeliveryItem> deliveryItemJoin =
                        root.join("items", JoinType.INNER);

                // DeliveryItem -> Item
                Join<DeliveryItem, Item> itemJoin =
                        deliveryItemJoin.join("item", JoinType.INNER);

                query.distinct(true);

                if (StringUtils.hasText(itemId)) {
                    predicates.add(cb.equal(
                            itemJoin.get("itemId"),
                            itemId
                    ));
                }

                if (StringUtils.hasText(itemDescription)) {
                    predicates.add(cb.like(
                            cb.lower(itemJoin.get("itemDescription")),
                            "%" + itemDescription.toLowerCase() + "%"
                    ));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
