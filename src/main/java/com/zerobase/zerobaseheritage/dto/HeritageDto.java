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
public class HeritageDto {

  private String heritageGrade;
  private String heritageId;
  private String heritageName;
  private Double latitude;
  private Double longitude;

   public static HeritageDto toDto(HeritageEntity heritageEntity) {
     return HeritageDto.builder()
         .heritageGrade(heritageEntity.getHeritageGrade())
         .heritageId(heritageEntity.getHeritageId())
         .heritageName(heritageEntity.getHeritageName())
         .longitude(heritageEntity.getLocation().getX())
         .latitude(heritageEntity.getLocation().getY())
         .build();
   }

}
