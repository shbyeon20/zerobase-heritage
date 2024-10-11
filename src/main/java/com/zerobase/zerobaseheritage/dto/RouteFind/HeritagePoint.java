package com.zerobase.zerobaseheritage.dto.RouteFind;

import com.zerobase.zerobaseheritage.datatype.HeritageGrade;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
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

  public static HeritagePoint fromDto(HeritageDto heritageDto) {
    return HeritagePoint.builder()
        .heritageGrade(heritageDto.getHeritageGrade())
        .heritageGradePoint(
            HeritageGrade.toGradePoint(heritageDto.getHeritageGrade()))
        .heritageId(heritageDto.getHeritageId())
        .heritageName(heritageDto.getHeritageName())
        .longitudeX(heritageDto.getLongitude())
        .latitudeY(heritageDto.getLatitude())
        .build();


  }
}

