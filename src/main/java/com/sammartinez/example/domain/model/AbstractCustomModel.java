package com.sammartinez.example.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AbstractCustomModel implements Serializable {

  @Serial private static final long serialVersionUID = 6256502687666885994L;

  private Integer id;
  @EqualsAndHashCode.Exclude private LocalDateTime created;
  @EqualsAndHashCode.Exclude private LocalDateTime updated;
}
