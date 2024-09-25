package com.zerobase.zerobaseheritage.dto;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiItem;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import org.springframework.data.geo.Point;

public class HeritageApiDto {

  private String heritageId; //
  private String heritageName;
  private Point Location;
  private String heritageGrade;


  public static HeritageEntity toEntiy(HeritageApiItem heritageApiItem) {
    return HeritageEntity.builder()
        .heritageId(heritageApiItem.getHeritageId())
        .heritageName(heritageApiItem.getHeritageName())
        .heritageGrade(heritageApiItem.getHeritageGrade())
        .build();
  }

}
