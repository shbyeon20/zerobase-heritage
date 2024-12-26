package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDto;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDtos;
import com.zerobase.zerobaseheritage.externalApi.PathFindApi;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteFindThreadService {

  private final PathFindApi pathFindApi;
  @Qualifier("ExternalApiTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;

  /*
  Point 들 간의 경로 정보를 API 로 부터 호출하여 반환한다
   */

  public Future<PathFindApiResultDtos> submitPathFindTask(
      PointCollection routePoints, HeritagePoint nextDestinationCandidate,
      CustomPoint clientPoint) {

    log.info(
        "RouteFindThreadService Submitting task for {} Active Threads: {}, Queue Size: {}",
        nextDestinationCandidate.toString(),
        taskExecutor.getActiveCount(),
        taskExecutor.getThreadPoolExecutor().getQueue());

    return taskExecutor.submit(

        new Callable<>() {

          @Override
          public PathFindApiResultDtos call()  {

            // Route 의 마지막 위치 ~  다음 이동 후보지 사이의 Path 정보 불러오기
            PathFindApiResultDto pathToHeritageCandidate = pathFindApi.getPathInfoBetweenPoints(
                routePoints.getPoints().getLast(), nextDestinationCandidate);
            // 다음 이동 후보지 ~ Client Location 사이의 Path 정보 불러오기
            PathFindApiResultDto pathToReturn = pathFindApi.getPathInfoBetweenPoints(
                nextDestinationCandidate, clientPoint);

            // API 호출정보를 병합하여 반환
            return PathFindApiResultDtos
                .builder()
                .nextDestinationCandidate(nextDestinationCandidate)
                .pathToHeritageCandidate(pathToHeritageCandidate)
                .pathToReturn(pathToReturn)
                .build();
          }
        });

  }


}



