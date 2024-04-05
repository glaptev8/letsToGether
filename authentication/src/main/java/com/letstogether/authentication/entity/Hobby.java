package com.letstogether.authentication.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.letstogether.dto.HobbyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table("hobby")
@NoArgsConstructor
@AllArgsConstructor
public class Hobby {
  @Id
  private Long id;
  private Long userId;
  private HobbyType hobby;
}
