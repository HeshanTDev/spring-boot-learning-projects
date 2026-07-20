package com.heshant.mapstruct.mapper;

import com.heshant.mapstruct.dto.EmployeeDTO;
import com.heshant.mapstruct.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring",uses = {DepartmentMapper.class})
public interface EmployeeMapper {

    //    @Mapping(target = "departmentName", source = "department.deptName")
    @Mapping(source = "empId", target = "id")
    @Mapping(source = "empName", target = "name")
    @Mapping(source = "departmentDTO", target = "department")
    Employee toEntity(EmployeeDTO employeeDTO);

//    @Mapping(target = "departmentName", source = "department.deptName")

    @Mappings({
            @Mapping(target = "empId", source = "id"),
            @Mapping(target = "empName", source = "name"),
            @Mapping(target = "departmentDTO", source = "department")})
    EmployeeDTO toDto(Employee employee);

    List<EmployeeDTO> toDTOList(List<Employee> employees);
}
