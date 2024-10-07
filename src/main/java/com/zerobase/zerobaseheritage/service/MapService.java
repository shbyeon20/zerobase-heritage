package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.datatype.MapBoundingBox;
import com.zerobase.zerobaseheritage.datatype.MapGrid;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.MapResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;


/*
client로부터 지도표시를 위해 위도선의 최대최소, 경도선의 최대최소 각각 4개 값을 받음
 1. 4개의 값으로 BoundingBox생성하고 이를 Grid로 분할함
 2. BoundingBox내 존재하는 문화유산을 확인함
 3. BoundingBox내 존재하는 방문문화유산을 확인함

 */
@Service
@RequiredArgsConstructor
public class MapService {

  private final GridService gridService;
  private final VisitService visitService;
  private final SearchService searchService;


  public List<MapGrid> createGridsWithColor(
      double north_Latitude, double south_Latitude,
      double east_Longitude, double west_Longitude, String userId
  ) {

    // bounding box 생성
    MapBoundingBox boundingBox = new MapBoundingBox(
        north_Latitude, south_Latitude, east_Longitude, west_Longitude);

    // 생성된 bounding box를 grid로 나눔
    List<MapGrid> grids = gridService.createGridsFromBoundingBox(boundingBox);

    // user가 방문한 heritage list를 호출
    List<HeritageDto> visitedHeritageDtos = visitService.visitedHeritageByUser(
        userId);

    // heritage들이 boundingbox에 있는지, 있다면 몇번째 grid에 속하는지 확인하여 unblack처리
    for (HeritageDto visitedHeritageDto : visitedHeritageDtos) {
      double xCoordinate = visitedHeritageDto.getLongitude();
      double yCoordinate = visitedHeritageDto.getLatitude();
      gridService.unBlackGridIfPointExistInGrid(xCoordinate, yCoordinate,
          boundingBox, grids);
    }

    return grids;

  }

  public MapResponse mapResponseWithGridsAndHeritages(String userId,
      Polygon polygon,
      double north_Latitude, double south_Latitude, double east_Longitude,
      double west_Longitude) {

    // polygon 내에 존재하는 heritage 검색
    List<HeritageDto> heritagesInBox = searchService.byPolygon(polygon);

    // coloredgrid생성
    List<MapGrid> gridsWithColor = createGridsWithColor(north_Latitude,
        south_Latitude, east_Longitude, west_Longitude, userId);

    // mapresponse에 담아서 반환
    return MapResponse.builder()
        .heritagesInBox(heritagesInBox)
        .mapGrids(gridsWithColor)
        .build();


  }


}
