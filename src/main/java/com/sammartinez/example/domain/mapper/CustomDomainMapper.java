package com.sammartinez.example.domain.mapper;

public interface CustomDomainMapper<M, E> {

  M toModel(E entity);

  E toEntity(M model);
}
