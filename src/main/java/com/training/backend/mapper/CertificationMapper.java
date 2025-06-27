package com.training.backend.mapper;

import com.training.backend.dto.CertificationDTO;
import com.training.backend.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertificationMapper {
    @Mapping(source = "certificationId", target = "certificationId")
    @Mapping(source = "certificationName", target = "certificationName")
    Certification toEntity(CertificationDTO entity);
    @Mapping(source = "certificationId", target = "certificationId")
    @Mapping(source = "certificationName", target = "certificationName")
    CertificationDTO toDto(Certification entity);
    List<CertificationDTO> toDtoList(List<Certification> list);
}
