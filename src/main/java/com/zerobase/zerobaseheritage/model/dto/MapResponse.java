package com.zerobase.zerobaseheritage.model.dto;

import com.zerobase.zerobaseheritage.model.MapGrid;
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
