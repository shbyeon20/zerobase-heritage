package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.datatype.exception.CustomException;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageResponseDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.entity.MemberEntity;
import com.zerobase.zerobaseheritage.entity.VisitedHeritageEntity;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import com.zerobase.zerobaseheritage.repository.MemberRepository;
import com.zerobase.zerobaseheritage.repository.VisitedHeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitService {

  private final HeritageRepository heritageRepository;
  private final VisitedHeritageRepository visitedHeritageRepository;
  private final MemberRepository memberRepository;
  private final GeoLocationAdapter geoLocationAdapter;

    /*
    유저가 방문하고자하는 유적을 선택하면 방문여부를 확인한 후, 방문처리한다.

    todo :
     1. SpringSecurity 도입 후 principal 로부터 ID 획득
     2. 로그인시 Visited Heritage Set 캐쉬에 저장하여 관리하도록 설정
     */

  @Transactional
  public HeritageResponseDto visitHeritage(String memberId, String heritageId) {

    log.info("visitHeritage Service start");

    if (visitedHeritageRepository.
        existsByMemberEntity_MemberIdAndHeritageEntity_HeritageId(memberId, heritageId)) {
      throw new CustomException(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
          "이미 방문처리한 유적입니다");
    }

    HeritageEntity heritageEntity = heritageRepository
        .findByHeritageId(heritageId)
        .orElseThrow(
            () -> new CustomException(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
                "존재하지 않는 유적 ID에 대한 요청입니다."));

    MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
        .orElseThrow(
            () -> new CustomException(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
                "존재하지 않는 유저 ID에 대한 요청입니다."));

    VisitedHeritageEntity newVisit = VisitedHeritageEntity.builder()
        .memberEntity(memberEntity)
        .heritageEntity(heritageEntity)
        .build();

    visitedHeritageRepository.save(newVisit);

    log.info("visitHeritage Service finished");

    return HeritageResponseDto.fromEntity(heritageEntity);
  }

  public List<HeritageResponseDto> visitedHeritageByUserWithinArea(String memberId,
      double northLatitude, double southLatitude, double eastLongitude,
      double westLongitude) {

    log.info("visitedHeritageByUser Service start");

    Polygon polygon = geoLocationAdapter.boxToPolygon(northLatitude,
        southLatitude, eastLongitude, westLongitude);

    List<HeritageEntity> visitedHeritages = visitedHeritageRepository.findAllVisitedHeritageByMemberIdWithinPolygon(
        memberId,polygon);

    return visitedHeritages.stream().map(HeritageResponseDto::fromEntity).toList();
  }
}
