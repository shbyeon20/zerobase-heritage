package com.zerobase.zerobaseheritage.dto;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageResponseDto {

  private String heritageGrade;
  private String heritageId;
  private String heritageName;
  private Double latitude;
  private Double longitude;

  public static HeritageResponseDto fromEntity(HeritageEntity heritageEntity) {
    return HeritageResponseDto.builder()
        .heritageGrade(heritageEntity.getHeritageGrade())
        .heritageId(heritageEntity.getHeritageId())
        .heritageName(heritageEntity.getHeritageName())
        .longitude(heritageEntity.getLocation().getX())
        .latitude(heritageEntity.getLocation().getY())
        .build();
  }

}
