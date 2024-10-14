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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteFindThreadService {

  private final PathFindApi pathFindApi;

  @Autowired
  @Qualifier("taskExecutor1")
  private ThreadPoolTaskExecutor taskExecutor;


  public Future<PathFindApiResultDtos> submitPathFindTask(
      PointCollection routePoints, HeritagePoint nextDestinationCandidate,
      CustomPoint clientPoint) {



    return taskExecutor.submit(
        new Callable<PathFindApiResultDtos>() {

      @Override
      public PathFindApiResultDtos call() throws Exception {

        PathFindApiResultDto pathToHeritageCandidate = pathFindApi.getPathInfoBetweenPoints(
            routePoints.getPoints().getLast(), nextDestinationCandidate);
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
