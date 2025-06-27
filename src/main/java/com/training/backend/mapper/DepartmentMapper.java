package com.training.backend.mapper;

import com.training.backend.dto.DepartmentDTO;
import com.training.backend.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    @Mapping(source = "departmentId", target = "departmentId")
    @Mapping(source = "departmentName", target = "departmentName")
    DepartmentDTO toDto(Department entity);
    @Mapping(source = "departmentId", target = "departmentId")
    @Mapping(source = "departmentName", target = "departmentName")
    Department toEntity(DepartmentDTO departmentDTO);
    List<DepartmentDTO> toDtoList(List<Department> list);
}
