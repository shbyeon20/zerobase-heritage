package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.externalApi.PathFindApi;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDto;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteFindService {
    private final PathFindApi pathFindApi;
    
    /*
    Point 들 간의 경로 정보를 API 로 부터 호출하여 반환한다
    */
    
    public PathFindApiResultDtos findTravelTimeToCandidateAndReturnToStart(
        BasePoint lastPoint, HeritagePoint nextDestinationCandidate, BasePoint clientPoint) {
        
        // Route 의 마지막 위치 ~  다음 이동 후보지 사이의 Path 정보 불러오기
        PathFindApiResultDto pathToTravelToNext = pathFindApi.getTravelTimeBetweenPoints(lastPoint, nextDestinationCandidate);
        // 다음 이동 후보지 ~ Client Location 사이의 Path 정보 불러오기
        PathFindApiResultDto pathToReturnToStart = pathFindApi.getTravelTimeBetweenPoints(nextDestinationCandidate, clientPoint);

        // API 호출정보를 병합하여 반환

        return PathFindApiResultDtos
            .builder()
            .nextDestinationCandidate(nextDestinationCandidate)
            .pathToHeritageCandidate(pathToTravelToNext)
            .pathToReturn(pathToReturnToStart)
            .build();
    }

}
