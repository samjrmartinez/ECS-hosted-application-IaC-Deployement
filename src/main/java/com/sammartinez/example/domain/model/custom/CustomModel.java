package com.sammartinez.example.domain.model.custom;

import com.sammartinez.example.domain.model.AbstractCustomModel;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomModel extends AbstractCustomModel {

  @Serial private static final long serialVersionUID = -9018594494022670476L;

  private Integer createdBy;
  private String title;
  private String description;
}
