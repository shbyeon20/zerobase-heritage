package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.MapBoundingBox;
import com.zerobase.zerobaseheritage.model.MapGrid;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MapGridService {

  // 위도 경도 0.01도를 기준으로 GRID 분할
  public static final double GRID_SIZE = 0.01;


  public List<MapGrid> createGrids(double northLatitude, double southLatitude
      , double eastLongitude, double westLongitude) {

    // bounding box 생성
    MapBoundingBox boundingBox = new MapBoundingBox(northLatitude, southLatitude, eastLongitude, westLongitude);

    // 생성된 bounding box를 grid로 나눔
    return  MapBoundingBox.createGridsFromBoundingBox(boundingBox);
  }

  public List<MapGrid> colorGridsIfVisited(List<MapGrid> grids, List<HeritageDto> visitedHeritageDtos) {
    for (HeritageDto heritage : visitedHeritageDtos) {
      for (MapGrid grid : grids) {
        MapGrid.unBlackGridIfPointExistInGrid(grid,heritage.getLongitude(), heritage.getLatitude());
      }
    }
    return grids;
  }
}
