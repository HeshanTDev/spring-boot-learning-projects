package com.heshant.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long empId;
    private String empName;
    private String email;
    private float salary;
    private DepartmentDTO departmentDTO;
}
