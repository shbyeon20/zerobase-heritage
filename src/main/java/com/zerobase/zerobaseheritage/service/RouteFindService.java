package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.externalApi.PathFindApi;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDto;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.DestinationTravelInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteFindService {

    private final PathFindApi pathFindApi;

    @Value("${sightseeing.time}")
    private static long SIGHT_SEEING_TIME;

    public DestinationTravelInfoDto getDestinationCandidateInfo(
        BasePoint lastPoint, HeritagePoint nextDestinationCandidate,
        BasePoint clientPoint) {


        // Route 의 마지막 위치 ~  다음 이동 후보지 사이의 Path 정보 불러오기
        PathFindApiResultDto pathToTravelToNext = pathFindApi.getTravelTimeBetweenPoints(
            lastPoint, nextDestinationCandidate);
        // 다음 이동 후보지 ~ Client Location 사이의 Path 정보 불러오기
        PathFindApiResultDto pathReturnToStart = pathFindApi.getTravelTimeBetweenPoints(
            nextDestinationCandidate, clientPoint);


        return DestinationTravelInfoDto
            .builder()
            .pathToNextCandidate(pathToTravelToNext)
            .pathReturnToStart(pathReturnToStart)
            .candidateDestination(nextDestinationCandidate)
            .build();
    }


    // API 에서 예외적인 값(차로 도달할 수 없는 경우 등)을 수령시 continue
    public boolean validateDestinationInfoNotEmpty(DestinationTravelInfoDto destinationCandidateInfoDto) {
        if (destinationCandidateInfoDto.getPathToNextCandidate() == null
            && destinationCandidateInfoDto.getPathReturnToStart() == null) {
            return true;
        }
        return false;

    }


    public boolean validateTravelTimeWithinLimit(DestinationTravelInfoDto destinationCandidateInfoDto, long timeLimit) {

        // 마지막위치 ~ 이동후보지 이동시간 + 관광시간 ~ 시작위치 복귀시간 구하기
        long travelAndReturnTime = destinationCandidateInfoDto.getPathToNextCandidate().getDuration()
            + destinationCandidateInfoDto.getPathReturnToStart().getDuration()
            + SIGHT_SEEING_TIME;

        // 총 시간이 시간제한을 넘으면 해당 도착지는 제외
        if (travelAndReturnTime>timeLimit) {
            return false;
        }
        else {
            destinationCandidateInfoDto.setTravelAndReturnTime(travelAndReturnTime);
            return true;
        }

    }

    public double evaluateRoute(DestinationTravelInfoDto destinationCandidateInfoDto) {
        int heritageGradePoint = destinationCandidateInfoDto.getCandidateDestination().getHeritageGradePoint();
        long travelAndReturnTime = destinationCandidateInfoDto.getTravelAndReturnTime();
        double destinationEvaluationScore = heritageGradePoint / (double) travelAndReturnTime;

        return destinationEvaluationScore;




    }
}
