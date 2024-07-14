package com.sammartinez.example.domain.mapper.custom;

import com.sammartinez.example.domain.entity.custom.CustomEntity;
import com.sammartinez.example.domain.mapper.CustomDomainMapper;
import com.sammartinez.example.domain.mapper.CustomDomainMapperConfig;
import com.sammartinez.example.domain.model.custom.CustomModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CustomDomainMapperConfig.class)
public interface CustomMapper extends CustomDomainMapper<CustomModel, CustomEntity> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  CustomModel mergeModel(CustomModel source, @MappingTarget CustomModel target);
}
