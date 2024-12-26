package com.zerobase.zerobaseheritage.model.dto.RouteFind;

import com.zerobase.zerobaseheritage.model.type.HeritageGrade;
import com.zerobase.zerobaseheritage.model.dto.HeritageResponseDto;
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
  private boolean alreadyUsed;

  public static HeritagePoint fromDto(HeritageResponseDto heritageResponseDto) {
    return HeritagePoint.builder()
        .heritageGrade(heritageResponseDto.getHeritageGrade())
        .heritageGradePoint(
            HeritageGrade.toGradePoint(heritageResponseDto.getHeritageGrade()))
        .heritageId(heritageResponseDto.getHeritageId())
        .heritageName(heritageResponseDto.getHeritageName())
        .longitudeX(heritageResponseDto.getLongitude())
        .latitudeY(heritageResponseDto.getLatitude())
        .build();


  }
}

