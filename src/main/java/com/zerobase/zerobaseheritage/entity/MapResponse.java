package com.zerobase.zerobaseheritage.entity;

import com.zerobase.zerobaseheritage.datatype.MapGrid;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MapResponse {

  List<HeritageDto> heritagesInBox;
  List<MapGrid> mapGrids;

}
