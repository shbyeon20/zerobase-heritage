package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.RoutePointsResponse;
import com.zerobase.zerobaseheritage.model.dto.HeritageDto;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.DestinationTravelInfoDto;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.model.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.repository.VisitedHeritageRepository;
import com.zerobase.zerobaseheritage.threading.RouteFindThread;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteFindManagementService {

  private final HeritageSearchService heritageSearchService;
  private final GeoLocationAdapter geoLocationAdapter;
  private final RouteFindThread routeFindThread;
  private final RouteFindService routeFindService;
  private final VisitedHeritageRepository visitedHeritageRepository;


    public RoutePointsResponse routeFind(String userId, double longitude, double latitude,
      long timeLimit) throws InterruptedException, ExecutionException {

    // point collection에 객체를 생성하고 client 위치를 collection에 담음
    PointCollection pointCollection = initializePointCollection(longitude, latitude);
    BasePoint startPoint = pointCollection.getPoints().getFirst();


    // clientPoint 주변의 heritagePoint 를 탐색하여 리스트 반환
    List<HeritagePoint> heritagePoints = this.getHeritagePoints(longitude, latitude);
     this.filterVisitedHeritageByUser(heritagePoints,userId);


    // timeLimit 내에 탐색 가능한 경로를 모두 확인하여, 최적의 경로를 pointCollection 에 추가함.
    boolean addSuccess = true;
    while (addSuccess) {
      addSuccess = evaluateHeritagePointAndAddToCollection
          (startPoint, heritagePoints, timeLimit, pointCollection);
    }

    return RoutePointsResponse.fromPointCollection(pointCollection);
  }

  private void filterVisitedHeritageByUser(List<HeritagePoint> heritagePoints, String userId) {
    List<HeritageEntity> visitedHeritage = visitedHeritageRepository.findAllVisitedHeritageByMemberId(userId);
    for (HeritagePoint heritagePoint : heritagePoints) {
      for (HeritageEntity heritageEntity : visitedHeritage) {
        if(Objects.equals(heritagePoint.getHeritageId(), heritageEntity.getHeritageId())){
         heritagePoint.setUnUsable(true);
        }
      }
    }
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
        heritageSearchService.findHeritagesDistancedFromPoint(longtitude, latitude);

    // heritageDto 를 HeritagePoint(CustomPoint)로 형변환
    return heritageDtos.stream().map(HeritagePoint::fromDto).toList();
  }


  /*
     외부 API 로부터 경로간의 정보(걸리는 시간)를 확인함
     - routePoints 의 마지막 경로에서 다음 경로까지, 그리고 다음경로에서 도착지로 돌아오는 시간 확인
     - 유적지의 등급점수를 관람시간으로 나누어 시간당 점수가 가장 높은 경로를 선택
     - 단, 관람시간이 timeLimit을 넘으면 경로선택에서 제외
      */
  private boolean evaluateHeritagePointAndAddToCollection(BasePoint clientPoint,
      List<HeritagePoint> heritagePoints, long timeLimit, PointCollection routePoints)
      throws InterruptedException, ExecutionException {


    // 멀티스레드 결과물을 담을 future list 생성하여 호출
    List<Future<DestinationTravelInfoDto>> futures = new LinkedList<>();

    for (HeritagePoint nextDestinationCandidate : heritagePoints) {
      if (nextDestinationCandidate.isUnUsable()) continue;

      futures.add(routeFindThread.submitGetDestinationCandidateInfoTask(
          routePoints.getPoints().getLast(), nextDestinationCandidate, clientPoint));
      Thread.sleep(300); // 외부 API 부하경감을 위한 sleep. 이 이상은 reject 발생
    }

    DestinationTravelInfoDto nextDestinationInfoDto = null;
    double evaluationScoreMax = 0;

    for (Future<DestinationTravelInfoDto> future : futures) {
      DestinationTravelInfoDto destinationCandidate = future.get();

      if(!routeFindService.validateDestinationInfoNotEmpty(destinationCandidate)) continue;
      if(!routeFindService.validateTravelTimeWithinLimit(destinationCandidate,timeLimit)) continue;
      double routeScore =routeFindService.evaluateRoute(destinationCandidate);

      // 효율도가 가장 높은 경로를 선택함
      if (routeScore > evaluationScoreMax) {
        evaluationScoreMax = routeScore;
        nextDestinationInfoDto = destinationCandidate;
      }
    }

    if (nextDestinationInfoDto == null) return false;
    includeCandidateToRoute(routePoints, nextDestinationInfoDto);
    return true;
  }

  private static void includeCandidateToRoute(PointCollection routePoints, DestinationTravelInfoDto nextDestinationInfoDto) {

    HeritagePoint nextDestination = nextDestinationInfoDto.getCandidateDestination();
    long travelTime =  nextDestinationInfoDto.getPathToNextCandidate().getDuration();
    long returnTime = nextDestinationInfoDto.getPathReturnToStart().getDuration();
    int gradePoint = nextDestinationInfoDto.getCandidateDestination().getHeritageGradePoint();

    // 선택된 nextDestination을 업데이트하고, 재탐색 방지
    routePoints.getPoints().add(nextDestination);
    nextDestination.setUnUsable(true);

    // route Point에 추가후 데이터 업데이트
    routePoints.setReturnTime(returnTime);
    routePoints.setRouteTimeSum(routePoints.getRouteTimeSum() + travelTime);
    routePoints.setHeritageGradePointSum(routePoints.getHeritageGradePointSum() + gradePoint);
  }


}
