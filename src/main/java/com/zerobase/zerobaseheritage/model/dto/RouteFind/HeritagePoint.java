package com.zerobase.zerobaseheritage.model.dto.RouteFind;

import com.zerobase.zerobaseheritage.model.type.HeritageGrade;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class HeritagePoint extends BasePoint {

  private String heritageGrade;
  private int heritageGradePoint;
  private String heritageId;
  private String heritageName;
  private boolean unUsable;

  public static HeritagePoint fromDto(HeritageDto heritageDto) {
    return HeritagePoint.builder()
        .heritageGrade(heritageDto.getHeritageGrade())
        .heritageGradePoint(HeritageGrade.toGradePoint(heritageDto.getHeritageGrade()))
        .heritageId(heritageDto.getHeritageId())
        .heritageName(heritageDto.getHeritageName())
        .longitudeX(heritageDto.getLongitude())
        .latitudeY(heritageDto.getLatitude())
        .build();


  }
}

