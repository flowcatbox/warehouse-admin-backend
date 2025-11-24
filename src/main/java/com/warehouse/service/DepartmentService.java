package com.warehouse.service;

import com.warehouse.entity.Department;
import com.warehouse.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Page<Department> findByDepartmentNameContainingIgnoreCase(
            Pageable pageable,
            String departmentName
    ){
        return departmentRepository.findAll((Specification<Department>) (root, query, criteriaBuilder)->{
           List<Predicate> predicates = new ArrayList<>();

           if(StringUtils.hasText(departmentName)){
               predicates.add(criteriaBuilder.like(
                       criteriaBuilder.lower(root.get("departmentName")),
                       "%" + departmentName.toLowerCase() + "%"
               ));
           }
           return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
