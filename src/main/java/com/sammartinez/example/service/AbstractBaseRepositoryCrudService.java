package com.sammartinez.example.service;

import com.sammartinez.example.domain.mapper.CustomDomainMapper;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

@Setter // For unit testing
public abstract class AbstractBaseRepositoryCrudService<
    M, E, R extends JpaRepository<E, Integer>, P extends CustomDomainMapper<M, E>> {

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Getter(AccessLevel.PUBLIC)
  private R repository;

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Getter(AccessLevel.PROTECTED)
  private P mapper;

  public M find(Integer id) {
    return getRepository().findById(id).map(this::toModel).orElse(null);
  }

  public List<M> findAll() {
    var entities = getRepository().findAll();
    return toModels(entities);
  }

  public Page<M> findAll(int page, int pageSize) {
    var entities = getRepository().findAll(PageRequest.of(page, pageSize));
    return toModels(entities);
  }

  public M save(M saving) {
    var entity = toEntity(saving);
    var saved = getRepository().save(entity);
    return toModel(saved);
  }

  public List<M> save(List<M> saving) {
    var entitiesUpdating = toEntities(saving);
    var saved = getRepository().saveAll(entitiesUpdating);
    return toModels(saved);
  }

  public void delete(Integer id) {
    var toDelete = find(id);

    if (toDelete != null) {
      getRepository().deleteById(id);
    }
  }

  public void delete(E entity) {
    if (entity != null) {
      getRepository().delete(entity);
    }
  }

  public void deleteAll(List<E> entities) {
    if (entities != null) {
      var filteredEntities = entities.stream().filter(Objects::nonNull).toList();
      getRepository().deleteAll(filteredEntities);
    }
  }

  public M toModel(E entity) {
    return getMapper().toModel(entity);
  }

  public E toEntity(M model) {
    return getMapper().toEntity(model);
  }

  public List<M> toModels(List<E> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public List<E> toEntities(List<M> models) {
    return models.stream().map(this::toEntity).toList();
  }

  public Page<M> toModels(Page<E> entities) {
    return entities.map(this::toModel);
  }
}
