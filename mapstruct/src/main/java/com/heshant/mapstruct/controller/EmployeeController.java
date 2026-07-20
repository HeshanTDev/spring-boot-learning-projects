package com.heshant.mapstruct.controller;

import com.heshant.mapstruct.dto.EmployeeDTO;
import com.heshant.mapstruct.dto.EmployeeDetailsDTO;
import com.heshant.mapstruct.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDTO> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployee = employeeService.save(employeeDTO);
        return ResponseEntity.ok(savedEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.findById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/employee-details/{id}")
    public ResponseEntity<EmployeeDetailsDTO> getEmployeeDetailsById(@PathVariable Long id){
        EmployeeDetailsDTO employeeById = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeById);
    }
}
