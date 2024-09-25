package com.zerobase.zerobaseheritage.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeritageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String heritageId; // API상의 '연계번호(ccbaCpno)'를 고유 ID로 사용
  private String heritageName;
  @Nullable
  private Point location;
  private String heritageGrade;
  @Nullable
  private String basicDescription;

}
