package com.sammartinez.example.service;

import static java.lang.Class.forName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Mockito.mock;

import com.sammartinez.example.AbstractBaseTest;
import com.sammartinez.example.domain.mapper.CustomDomainMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.runner.RunWith;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public abstract class AbstractServiceTest extends AbstractBaseTest {

  protected JpaRepository<?, Integer> mockedRepository;

  protected <
          M,
          E,
          R extends JpaRepository<E, Integer>,
          P extends CustomDomainMapper<M, E>,
          T extends AbstractBaseRepositoryCrudService<M, E, R, P>>
      void setUp(final T serviceUnderTest) {
    assertNotNull(serviceUnderTest);
    var serviceParameterTypes = getServiceParameterTypes(serviceUnderTest);
    R repositoryClass = getMockedRepositoryClass(serviceParameterTypes);
    assertNotNull(repositoryClass);
    serviceUnderTest.setRepository(repositoryClass);
    mockedRepository = repositoryClass;

    P mapperClass = getMapperClass(serviceParameterTypes);
    assertNotNull(mapperClass);
    serviceUnderTest.setMapper(mapperClass);
  }

  private Type[] getServiceParameterTypes(
      final AbstractBaseRepositoryCrudService<?, ?, ?, ?> serviceUnderTest) {
    return ((ParameterizedType) serviceUnderTest.getClass().getGenericSuperclass())
        .getActualTypeArguments();
  }

  @SuppressWarnings("unchecked")
  private <E, R extends JpaRepository<E, Integer>> R getMockedRepositoryClass(
      final Type[] serviceParameterTypes) {
    try {
      var repositoryType = getRepositoryParameterType(serviceParameterTypes);
      var repositoryClass = forName(repositoryType);
      return (R) mock(repositoryClass);
    } catch (ClassNotFoundException ex) {
      return null;
    }
  }

  private String getRepositoryParameterType(final Type[] serviceParameterTypes) {
    return serviceParameterTypes[2].getTypeName();
  }

  @SuppressWarnings("unchecked")
  private <M, E, P extends CustomDomainMapper<M, E>> P getMapperClass(
      final Type[] serviceParameterTypes) {
    try {
      var mapperType = getMapperParameterType(serviceParameterTypes);
      var mapperClass = forName(mapperType);
      return (P) getMapper(mapperClass);
    } catch (ClassNotFoundException ex) {
      return null;
    }
  }

  private String getMapperParameterType(final Type[] serviceParameterTypes) {
    return serviceParameterTypes[3].getTypeName();
  }
}
