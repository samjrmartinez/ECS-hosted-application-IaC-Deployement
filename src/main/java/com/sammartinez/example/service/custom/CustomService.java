package com.sammartinez.example.service.custom;

import com.sammartinez.example.domain.entity.custom.CustomEntity;
import com.sammartinez.example.domain.model.custom.CustomModel;
import com.sammartinez.example.service.AbstractBaseRepositoryCrudService;
import com.sammartinez.example.domain.mapper.custom.CustomMapper;
import com.sammartinez.example.domain.repository.custom.CustomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
import org.springframework.stereotype.Service;

@Service
@Flogger
@RequiredArgsConstructor
public class CustomService
    extends AbstractBaseRepositoryCrudService<
        CustomModel, CustomEntity, CustomRepository, CustomMapper> {
  public void save(Object o, Integer mockId, Integer mockId1) {
  }
}
