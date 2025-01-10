package com.zerobase.zerobaseheritage.model.dto.heritageApi;

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


}

