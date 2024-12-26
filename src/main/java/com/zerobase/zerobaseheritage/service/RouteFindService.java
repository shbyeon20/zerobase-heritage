package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.exception.CustomException;
import com.zerobase.zerobaseheritage.model.exception.ErrorCode;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.model.dto.RoutePointsResponse;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDtos;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteFindService {

  private final SearchHeritageService searchHeritageService;
  private final GeoLocationAdapter geoLocationAdapter;
  private final RouteFindThreadService routeFindThreadService;


  @Value("${sightseeing.time}")
  private long SIGHT_SEEING_TIME;

  /*

  1. 나의 위치를 기반으로 heritage list 를 받는다.

  2. heritage list 를 Point 로 변환하고, 그 중 가장 최적의 행선지를 고르고 이를 자료구조에 저장

  3. 자료구조를 응답 DTO 로 변환하여 Client 에 응답

   */

  public RoutePointsResponse routeFind(CustomPoint clientPoint,
      long timeLimit) {

    log.info("routeFind Service started for clientPoint {} with timeLimit={}",
        clientPoint, timeLimit);

    // 경로상의 Points 를 담는 컬랙션을 생성하고 출발점 clientPoint 를 넣어서 초기화
    PointCollection pointCollection = new PointCollection();
    LinkedList<BasePoint> points = pointCollection.getPoints();
    points.add(clientPoint);

    // clientPoint 주변의 heritagePoint 를 탐색하여 리스트 반환
    List<HeritagePoint> heritagePoints = getHeritagePoints(clientPoint);

    // timeLimit 내에 탐색 가능한 경로를 모두 확인하여, 최적의 경로를 pointCollection 에 추가함.
    // 모두 추가할때까지 while 문 반복
    boolean IsThereMorePathToFind = true;
    while (IsThereMorePathToFind) {
      IsThereMorePathToFind = findNextPoint(clientPoint, heritagePoints,
          timeLimit, pointCollection);
    }
    return RoutePointsResponse.fromPointCollection(pointCollection);
  }

  /*
  client Location 주변의 heritage 를 point 로 변환하여 반환한다.
   */
  private List<HeritagePoint> getHeritagePoints(CustomPoint clientLocation) {

    log.info("getHeritagePoints service started for clientLocation={}",
        clientLocation);

    // CustomPoint 를 jtsPoint 로 형변환 후 Point 주변 문화유산 탐색
    Point clientPoint = geoLocationAdapter.coordinateToPoint(
        clientLocation.getLongitudeX(), clientLocation.getLatitudeY());
    List<HeritageDto> heritageDtos =
        searchHeritageService.ConvertToPointAndFindDistancedFrom(clientPoint.getCoordinate().getX(),clientPoint.getCoordinate().getY());

    // heritageDto 를 HeritagePoint(CustomPoint)로 형변환
    return heritageDtos.stream().map(HeritagePoint::fromDto).toList();
  }


  /*
     외부 API 로부터 경로간의 정보(걸리는 시간)를 확인함
     - routePoints 의 마지막 경로에서 다음 경로까지, 그리고 다음경로에서 도착지로 돌아오는 시간 확인
     - 유적지의 등급점수를 관람시간으로 나누어 시간당 점수가 가장 높은 경로를 선택
     - 단, 관람시간이 timeLimit을 넘으면 경로선택에서 제외
      */
  private boolean findNextPoint(CustomPoint clientPoint,
      List<HeritagePoint> heritagePoints, long timeLimit,
      PointCollection routePoints)  {
    log.info(
        "findNextPoint service started for clientPoint {} and routePoints={}",
        clientPoint, routePoints);

    // 멀티스레드 결과물을 담을 future list 생성하여 호출
    List<Future<PathFindApiResultDtos>> futures = new LinkedList<>();
    for (HeritagePoint nextDestinationCandidate : heritagePoints) {
      // heritagePoint 가 RoutePoints 에 이미 포함되어있다면 continue
      if (nextDestinationCandidate.isAlreadyUsed()) {
        continue;
      }
      futures.add(routeFindThreadService.submitPathFindTask(
          routePoints, nextDestinationCandidate, clientPoint));
      try {
        Thread.sleep(300); // 외부 API 부하경감을 위한 sleep. 이 이상은 reject 발생
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    // Future 에 담긴 HeritagePoint 와 관련된 API 결과값을 순회하여, HeritagePoint 별로
    // timeGradeRatio 를 계산하고 그 중 최대의 Point 를 다음 도착지로 결정함

    HeritagePoint nextDestination = null;
    PathFindApiResultDtos result;
    double timeGradeRatioMax = 0;
    long nextTime = 0;
    int nextGradePoint = 0;

    for (Future<PathFindApiResultDtos> future : futures) {
      try {
        result = future.get();
      } catch (Exception e) {
        e.printStackTrace();
        throw new CustomException(ErrorCode.THREAD_EXCEPTION,
            "pathFind 쓰레드 상에서 예외 발생");
      }

      // API 에서 예외적인 값(차로 도달할 수 없는 경우 등)을 수령시 continue
      if (result.getPathToHeritageCandidate() == null
          || result.getPathToReturn() == null) {
        continue;
      }

      long nextDestinationTime = result.getPathToHeritageCandidate()
          .getDuration();

      long returnTime = result.getPathToReturn().getDuration();

      // 현재까지 경로시간 + 다음목적지까지시간 + 복귀시간이 시간제한을 넘으면 안됨
      long timeAdded = nextDestinationTime + returnTime + SIGHT_SEEING_TIME;
      long routeTimeAddedAll = timeAdded + routePoints.getRouteTimeSum();
      if (routeTimeAddedAll > timeLimit) {
        continue;
      }

      //  문화유산점수/(다음목적지까지시간 + 복귀시간)의 효율도 구하기
      int heritageGradePoint = result.getNextDestinationCandidate()
          .getHeritageGradePoint();
      double durationGradeRatio = heritageGradePoint / (double) timeAdded;

      // 효율도가 가장 높은 경로를 선택함
      if (durationGradeRatio > timeGradeRatioMax) {
        timeGradeRatioMax = durationGradeRatio;

        nextDestination = result.getNextDestinationCandidate();
        nextTime = nextDestinationTime;
        nextGradePoint = heritageGradePoint;
        routePoints.setReturnTime(returnTime);
      }
    }

    // nextDestination이 Null로 유지되면 더이상 선택할 heritage가 부재한 것.
    if (nextDestination == null) {
      return false;
    }

    // 선택된 nextDestination을 업데이트하고, 재탐색 방지
    nextDestination.setAlreadyUsed(true);
    routePoints.getPoints().add(nextDestination);
    routePoints.setRouteTimeSum(routePoints.getRouteTimeSum() + nextTime);
    routePoints.setHeritageGradePointSum(
        routePoints.getHeritageGradePointSum() + nextGradePoint);

    return true;
  }


}
