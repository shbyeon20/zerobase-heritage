package com.zerobase.zerobaseheritage.threading;


import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDtos;
import com.zerobase.zerobaseheritage.service.RouteFindService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteFindThread {

  @Qualifier("ExternalApiTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;
  private final RouteFindService routeFindService;


  public Future<PathFindApiResultDtos> submitFindPointsTravelTimeTask(
      BasePoint lastPoint, HeritagePoint nextDestinationCandidate, BasePoint clientPoint) {

    return taskExecutor.submit(

        new Callable<>() {
          @Override
          public PathFindApiResultDtos call()  {
              return routeFindService.findTravelTimeToCandidateAndReturnToStart(lastPoint,nextDestinationCandidate,clientPoint);


          }
        });

  }




}



