package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.model.MapGrid;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import com.zerobase.zerobaseheritage.model.dto.MapResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MapFacadeService {

  private final GridService gridService;
  private final VisitHeritageService visitHeritageService;
  private final SearchHeritageService searchHeritageService;
  private final GeoLocationAdapter geoLocationAdapter;


  public List<MapGrid> createGridsAndColorIfVisited(
      double northLatitude, double southLatitude,
      double eastLongitude, double westLongitude, String userId
  ) {
    log.info("createGridsWithColor service start");

    List<MapGrid> grids = gridService.createGrids(northLatitude, southLatitude, eastLongitude, westLongitude);

    // user가 방문한 heritage list를 호출
    List<HeritageDto> visitedHeritageDtos = visitHeritageService
        .findVisitsWithinBoxByUser(userId, northLatitude, southLatitude, eastLongitude, westLongitude);

    List<MapGrid> coloredGrids = gridService.colorGridsIfVisited(grids, visitedHeritageDtos);



    log.info("createGridsWithColor service finish");

    return coloredGrids;

  }

  public MapResponse createMapResponseDtoWithColoredGridsAndHeritages(
      String userId, double northLatitude, double southLatitude, double eastLongitude,
      double westLongitude) {

    log.info("mapResponseWithGridsAndHeritages service start");

    Polygon polygon = geoLocationAdapter.convertToPolygon(
        northLatitude, southLatitude, eastLongitude, westLongitude);

    // polygon 내에 존재하는 heritage 검색
    List<HeritageDto> heritagesInBox = searchHeritageService.findHeritageWithinPolygon(polygon);

    // coloredgrid생성
    List<MapGrid> gridsWithColor = this.createGridsAndColorIfVisited(northLatitude,
        southLatitude, eastLongitude, westLongitude, userId);

    log.info("mapResponseWithGridsAndHeritages service finish");

    // mapresponse에 담아서 반환
    return MapResponse.builder()
        .heritagesInBox(heritagesInBox)
        .mapGrids(gridsWithColor)
        .build();


  }


}
