package com.warehouse.repository;

import com.warehouse.entity.Department;
import com.warehouse.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{
    Department findByDepartmentCode(String departmentCode);
    Page<Department> findAll(Specification<Department> specification, Pageable pageable);
    List<Department> findByDepartmentCodeIn(List<String> departmentCodes);
}
