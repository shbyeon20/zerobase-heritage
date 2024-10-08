package com.zerobase.zerobaseheritage.dto;

import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageApiDto {

  private String heritageId; // 일련번호
  private String heritageName;
  private Point location;
  private String heritageGrade;


  public static HeritageEntity toEntity(HeritageApiDto heritageApiDto) {
    return HeritageEntity.builder()
        .heritageId(heritageApiDto.heritageId)
        .heritageName(heritageApiDto.heritageName)
        .heritageGrade(heritageApiDto.heritageGrade)
        .location(heritageApiDto.location)
        .build();
  }


  public static HeritageApiDto fromEntity(HeritageEntity heritageEntity) {
    return HeritageApiDto.builder()
        .heritageId(heritageEntity.getHeritageId())
        .heritageName(heritageEntity.getHeritageName())
        .heritageGrade(heritageEntity.getHeritageGrade())
        .location(heritageEntity.getLocation())
        .build();
  }
}

