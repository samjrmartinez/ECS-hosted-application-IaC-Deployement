package com.sammartinez.example.domain.repository.custom;

import com.sammartinez.example.domain.entity.custom.CustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomRepository extends JpaRepository<CustomEntity, Integer> {}
