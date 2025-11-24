package com.warehouse.controller;

import com.warehouse.entity.Department;
import com.warehouse.service.DepartmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<?> getDepartments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String departmentName) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Department> departmentPage = departmentService.findByDepartmentNameContainingIgnoreCase(pageable, departmentName);

        Map<String, Object> response = new HashMap<>();
        response.put("list", departmentPage.getContent());
        response.put("total", departmentPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", departmentPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
