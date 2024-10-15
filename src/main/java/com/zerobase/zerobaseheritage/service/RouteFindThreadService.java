package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResultDto;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResultDtos;
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
  @Qualifier("RouteFindTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;


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

            // Route의 마지막점 ~ Candidate 사이의 Path 정보
            PathFindApiResultDto pathToHeritageCandidate = pathFindApi.getPathInfoBetweenPoints(
                routePoints.getPoints().getLast(), nextDestinationCandidate);
            // Candidate ~ Client Location 사이의 Path 정보
            PathFindApiResultDto pathToReturn = pathFindApi.getPathInfoBetweenPoints(
                nextDestinationCandidate, clientPoint);

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
