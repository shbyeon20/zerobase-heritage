package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResultDto;
import com.zerobase.zerobaseheritage.externalApi.PathFindApi;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteFindService {

  private final PathFindApi pathFindApi;
  private final SearchService searchService;
  private final GeoLocationAdapter geoLocationAdapter;

  @Value("${sightseeing.time}")
  private long SIGHT_SEEING_TIME;

  /*

  input : startPoint, he

  1. 나의 위치를 기반으로 heritage list를 받는다.

  2. heritage list를 Point로 변환하고, 그 중 가장 최적의 행선지를 고르고 이를 자료구조에 저장

  3. 자료구조를 변환하여 응답

  todo :
    1. PointCollection을 PathCollection으로 확장할지 여부 고민, 시간남으면?


   */
  public RoutePointsResponse routeFind(CustomPoint clientPoint, long timeLimit) {

    log.info("routeFind Service started for clientPoint " + clientPoint+" timeLimit="+timeLimit);

    // 경로상의 Points를 담는 컬랙션을 생성하고 출발점 clientPoint를 넣어서 초기화
    PointCollection pointCollection = new PointCollection();
    LinkedList<BasePoint> points = pointCollection.getPoints();
    points.add(clientPoint);

    // clientPoint 주변의 heritagePoint를 탐색하여 리스트 반환
    List<HeritagePoint> heritagePoints = getHeritagePoints(clientPoint);

    // timeLimit 내에 가능한 경로를 모두 추가할때까지 while문 반복
    boolean morePathToFind = true;
    while(morePathToFind){
      morePathToFind = findNextPoint(clientPoint, heritagePoints,
          timeLimit, pointCollection);
    }
    return RoutePointsResponse.fromPointCollection(pointCollection);
  }

  /*
  client Location 주변의 heritage를 point로 변환하여
   */
  private List<HeritagePoint> getHeritagePoints(CustomPoint clientLocation) {

    log.info("getHeritagePoints service started for clientLocation="+clientLocation);
    // CustomPoint를 jts Point로 형변환 후 Point 주변 문화유산 탐색
    Point clientPoint = geoLocationAdapter.coordinateToPoint(
        clientLocation.getLongitudeX(), clientLocation.getLatitudeY());
    List<HeritageDto> heritageDtos = searchService.byPointLocation(clientPoint);

    // heritageDto를 HeritagePoint(CustomPoint)로 형변환
    return heritageDtos.stream().map(HeritagePoint::fromDto).toList();
  }


  /*
     외부 API로 부터
     - 다음 경로까지의, 다음경로에서 도착지로 돌아오는 시간 확인
     -
      */
  private boolean findNextPoint(CustomPoint clientPoint,
      List<HeritagePoint> heritagePoints,
      long timeLimit, PointCollection routePoints) {
    log.info("findNextPoint service started for clientPoint "+clientPoint+"routePoints="+routePoints.toString());

    HeritagePoint nextDestination = null;
    double timeGradeRatioMax = 0;
    long nextTime=0;
    int nextGradePoint=0;

    // heritage를 순회하여 루트별로 timeGradeRatio를 계산, 최대 루트를 다음 도착지로 결정함
    for (HeritagePoint nextDestinationCandidate : heritagePoints) {
      // heritage가 이미 사용되었으면 continue
      if (nextDestinationCandidate.isAlreadyUsed()) continue;


      // 다음 목적지까지의 소요시간과 다음 목적지에서 client 위치까지 복귀시간 호출
      PathFindApiResultDto pathToHeritageCandidate = pathFindApi.getPathInfoBetweenPoints(
          routePoints.getPoints().getLast(), nextDestinationCandidate);
      PathFindApiResultDto pathToReturn = pathFindApi.getPathInfoBetweenPoints(
          nextDestinationCandidate, clientPoint);

      // API에서 예외적인 값(차로 도달할 수 없음)을 수령시 continue
      if(pathToHeritageCandidate==null||pathToReturn==null)continue;

      long nextDestinationTime = pathToHeritageCandidate.getDuration();

      long returnTime = pathToReturn.getDuration();





      // 현재까지 경로시간 + 다음목적지까지시간 + 복귀시간이 시간제한을 넘으면 안됨
      long timeAdded = nextDestinationTime + returnTime+ SIGHT_SEEING_TIME;
      long routeTimeAddedAll = timeAdded + routePoints.getRouteTimeSum();
      if (routeTimeAddedAll > timeLimit) continue;

      //  문화유산점수/(다음목적지까지시간 + 복귀시간)의 효율도 구하기
      int heritageGradePoint = nextDestinationCandidate.getHeritageGradePoint();
      double durationGradeRatio = heritageGradePoint / (double) timeAdded;

      // 효율도가 가장 높은 경로를 선택함
      if (durationGradeRatio > timeGradeRatioMax) {
        timeGradeRatioMax = durationGradeRatio;

        nextDestination = nextDestinationCandidate;
        nextTime = nextDestinationTime;
        nextGradePoint = heritageGradePoint;
        routePoints.setReturnTime(returnTime);
      }
    }

    // nextDestination이 Null이면 선택할 heritage가 더이상 부재한 것.
    if (nextDestination == null) return false;

    // 선택된 nextDestination을 업데이트하고, 재탐색 방지
    nextDestination.setAlreadyUsed(true);
    routePoints.getPoints().add(nextDestination);
    routePoints.setRouteTimeSum(routePoints.getRouteTimeSum()+nextTime);
    routePoints.setHeritageGradePointSum(routePoints.getHeritageGradePointSum()+nextGradePoint);


    return true;
  }


}
