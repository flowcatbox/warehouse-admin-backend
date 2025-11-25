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

    public Department createDepartment(Department department){
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department departmentDetail){
        return departmentRepository.findById(id)
                .map(department ->{
                    department.setDepartmentName(departmentDetail.getDepartmentName());
                    department.setDepartmentCode(departmentDetail.getDepartmentCode());
                    return departmentRepository.save(department);
                })
                .orElse(null);
    }

    public boolean deleteDepartment(Long id){
        if(departmentRepository.findById(id).isPresent()){
            departmentRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}
