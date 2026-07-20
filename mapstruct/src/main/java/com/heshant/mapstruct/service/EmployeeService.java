package com.heshant.mapstruct.service;

import com.heshant.mapstruct.dto.EmployeeDTO;
import com.heshant.mapstruct.dto.EmployeeDetailsDTO;
import com.heshant.mapstruct.entity.Department;
import com.heshant.mapstruct.entity.Employee;
import com.heshant.mapstruct.mapper.EmployeeDetailsMapper;
import com.heshant.mapstruct.mapper.EmployeeMapper;
import com.heshant.mapstruct.repository.DepartmentRepository;
import com.heshant.mapstruct.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeDetailsMapper employeeDetailsMapper;

    public EmployeeDTO save(EmployeeDTO employeeDTO) {

        Employee entity = employeeMapper.toEntity(employeeDTO);

        Department department = departmentRepository.findById(employeeDTO.getDepartmentDTO().getId()).orElseThrow(
                () -> new RuntimeException("Department not found!"));

        entity.setDepartment(department);

        Employee save = employeeRepository.save(entity);

        return employeeMapper.toDto(save);
    }

    public EmployeeDTO findById(Long id) {
        return employeeMapper.toDto(employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found!")));
    }

    public EmployeeDetailsDTO getEmployeeById(Long id) {
//        return employeeDetailsMapper.toEmployeeDetailsDTO(employeeRepository.findById(id).orElseThrow(()->new RuntimeException("Employee not found!")));
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Emplyee not found"));
        return employeeDetailsMapper.toEmployeeDetailsDTO(employee, employee.getDepartment());
    }
}

