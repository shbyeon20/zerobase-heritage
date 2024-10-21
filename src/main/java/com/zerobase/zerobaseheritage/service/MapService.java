package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.datatype.MapBoundingBox;
import com.zerobase.zerobaseheritage.datatype.MapGrid;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.dto.MapResponse;
import java.util.ArrayList;
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
public class MapService {

  private final GridService gridService;
  private final VisitService visitService;
  private final SearchService searchService;


  public List<MapGrid> selectGridsWithColor(
      double northLatitude, double southLatitude,
      double eastLongitude, double westLongitude, String userId
  ) {
    log.info("createGridsWithColor service start");

    // bounding box 생성
    MapBoundingBox boundingBox = new MapBoundingBox(
        northLatitude, southLatitude, eastLongitude, westLongitude);

    // 생성된 bounding box를 grid로 나눔
    List<MapGrid> grids = gridService.createGridsFromBoundingBox(boundingBox);

    // user가 방문한 heritage list를 호출
    List<HeritageDto> visitedHeritageDtos = new ArrayList<>(
        visitService.visitedHeritageByUser(userId));

    log.info(visitedHeritageDtos.toString());
    ;

    // api test 를 위해서 user data 임시로 생성하여 확인
    visitedHeritageDtos.add(HeritageDto.builder()
        .heritageGrade("국보")
        .heritageId("1111100020000")
        .heritageName("서울 원각사지 십층석탑")
        .latitude(37.5715461695449)
        .longitude(126.988207994364)
        .build());

    visitedHeritageDtos.add(HeritageDto.builder()
        .heritageGrade("보물")
        .heritageId("1121100030000")
        .heritageName("서울 원각사지 대원각사비")
        .latitude(37.5710693985849)
        .longitude(126.988634645148)
        .build());

    log.info(visitedHeritageDtos.toString());

    // heritage들이 boundingbox에 있는지, 있다면 몇번째 grid에 속하는지 확인하여 unblack처리
    for (HeritageDto visitedHeritageDto : visitedHeritageDtos) {
      double xCoordinate = visitedHeritageDto.getLongitude();
      double yCoordinate = visitedHeritageDto.getLatitude();
      gridService.unBlackGridIfPointExistInGrid(xCoordinate, yCoordinate,
          boundingBox, grids);
    }

    log.info("createGridsWithColor service finish");

    return grids;

  }

  /*
  한 User 당 맵 조회가 축척 변경, box 위치 변경 등 다회 이루어 질 것으로 예상되어
   관련 데이터 캐쉬처리하여 빠른 응답 리팩터링

  1. 유저의 방문한 유적 캐쉬처리 ( 기존 : where 조건절로 boundingBox 내의 방문유적만 조회
   -> 변경 : boundingBox와 무관하게 전체 1회 조회하여 cache에 저장 후 재활용)

  2. 유적지 조회가 많을 것으로 예상되는 관광지 (서울, 경주) 지역의 데이터 캐쉬서버 로딩처리

   */
  public MapResponse mapResponseWithGridsAndHeritages(String userId,
      Polygon polygon,
      double north_Latitude, double south_Latitude, double east_Longitude,
      double west_Longitude) {

    log.info("mapResponseWithGridsAndHeritages service start");

    // polygon 내에 존재하는 heritage 검색
    List<HeritageDto> heritagesInBox = searchService.byPolygon(polygon);

    // coloredgrid생성
    List<MapGrid> gridsWithColor = this.selectGridsWithColor(north_Latitude,
        south_Latitude, east_Longitude, west_Longitude, userId);

    log.info("mapResponseWithGridsAndHeritages service finish");

    // mapresponse에 담아서 반환
    return MapResponse.builder()
        .heritagesInBox(heritagesInBox)
        .mapGrids(gridsWithColor)
        .build();


  }


}
