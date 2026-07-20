package com.heshant.mapstruct.mapper;

import com.heshant.mapstruct.dto.DepartmentDTO;
import com.heshant.mapstruct.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(DepartmentDTO departmentDTO);
    DepartmentDTO toDto(Department department);

}
