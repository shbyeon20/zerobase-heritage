package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.RoutePointsResponse;
import com.zerobase.zerobaseheritage.model.exception.CustomException;
import com.zerobase.zerobaseheritage.model.exception.ErrorCode;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDtos;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.threading.RouteFindThread;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteFindManagementService {

  private final HeritageSearchService heritageSearchService;
  private final GeoLocationAdapter geoLocationAdapter;
  private final RouteFindThread routeFindThread;


  @Value("${sightseeing.time}")
  private long SIGHT_SEEING_TIME;


  public RoutePointsResponse routeFind(String userId, double longitude, double latitude,
      long timeLimit) throws InterruptedException, ExecutionException {

    // point collection에 객체를 생성하고 client 위치를 collection에 담음
    PointCollection pointCollection = initializePointCollection(longitude, latitude);

    // clientPoint 주변의 heritagePoint 를 탐색하여 리스트 반환
    List<HeritagePoint> heritagePoints = this.getHeritagePoints(longitude, latitude);

    // timeLimit 내에 탐색 가능한 경로를 모두 확인하여, 최적의 경로를 pointCollection 에 추가함.
    boolean IsThereMorePathToFind = true;
    while (IsThereMorePathToFind) {
      IsThereMorePathToFind = addNextDestinationToPointCollection(pointCollection.getPoints().getFirst(), heritagePoints,
          timeLimit, pointCollection);
    }

    return RoutePointsResponse.fromPointCollection(pointCollection);
  }

  private PointCollection initializePointCollection(double longitude,
      double latitude) {
    CustomPoint clientPoint = geoLocationAdapter.convertToCustomPoint(longitude,
        latitude);

    // 경로상의 Points 를 담는 컬랙션을 생성하고 출발점 clientPoint 를 넣어서 초기화
    PointCollection pointCollection = new PointCollection();


    LinkedList<BasePoint> points = pointCollection.getPoints();
    points.add(clientPoint);
    return pointCollection;
  }

  /*
  client Location 주변의 heritage 를 point 로 변환하여 반환한다.
   */
  private List<HeritagePoint> getHeritagePoints(double longtitude, double latitude) {

    List<HeritageDto> heritageDtos =
        heritageSearchService.findHeritagesDistancedFromPoint(longtitude, longtitude);

    // heritageDto 를 HeritagePoint(CustomPoint)로 형변환
    return heritageDtos.stream().map(HeritagePoint::fromDto).toList();
  }


  /*
     외부 API 로부터 경로간의 정보(걸리는 시간)를 확인함
     - routePoints 의 마지막 경로에서 다음 경로까지, 그리고 다음경로에서 도착지로 돌아오는 시간 확인
     - 유적지의 등급점수를 관람시간으로 나누어 시간당 점수가 가장 높은 경로를 선택
     - 단, 관람시간이 timeLimit을 넘으면 경로선택에서 제외
      */
  private boolean addNextDestinationToPointCollection(BasePoint clientPoint,
      List<HeritagePoint> heritagePoints, long timeLimit, PointCollection routePoints)
      throws InterruptedException, ExecutionException {


    // 멀티스레드 결과물을 담을 future list 생성하여 호출
    List<Future<PathFindApiResultDtos>> futures = new LinkedList<>();

    for (HeritagePoint nextDestinationCandidate : heritagePoints) {
      if (nextDestinationCandidate.isAlreadyInCollection()) continue;
      futures.add(routeFindThread.submitFindPointsTravelTimeTask(routePoints.getPoints().getLast(), nextDestinationCandidate, clientPoint));
      Thread.sleep(300); // 외부 API 부하경감을 위한 sleep. 이 이상은 reject 발생
    }

    // Future 에 담긴 HeritagePoint 와 관련된 API 결과값을 순회하여, HeritagePoint 별로
    // timeGradeRatio 를 계산하고 그 중 최대의 Point 를 다음 도착지로 결정함

    HeritagePoint nextDestination = null;
    PathFindApiResultDtos result;
    double timeGradeRatioMax = 0;
    long nextTime = 0;
    int nextGradePoint = 0;

    for (Future<PathFindApiResultDtos> future : futures) {
        result = future.get();

      // API 에서 예외적인 값(차로 도달할 수 없는 경우 등)을 수령시 continue
      if (result.getPathToHeritageCandidate() == null
          || result.getPathToReturn() == null) {
        continue;
      }

      long nextDestinationTime = result.getPathToHeritageCandidate().getDuration();

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
    nextDestination.setAlreadyInCollection(true);
    routePoints.getPoints().add(nextDestination);
    routePoints.setRouteTimeSum(routePoints.getRouteTimeSum() + nextTime);
    routePoints.setHeritageGradePointSum(
        routePoints.getHeritageGradePointSum() + nextGradePoint);

    return true;
  }


}
