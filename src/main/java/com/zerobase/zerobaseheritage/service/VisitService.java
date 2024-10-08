package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.entity.MemberEntity;
import com.zerobase.zerobaseheritage.entity.VisitedHeritageEntity;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import com.zerobase.zerobaseheritage.repository.MemberRepository;
import com.zerobase.zerobaseheritage.repository.VisitedHeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitService {

  private final HeritageRepository heritageRepository;
  private final VisitedHeritageRepository visitedHeritageRepository;
  private final MemberRepository memberRepository;

    /*
    유저가 방문하고자하는 유적을 선택하면 방문여부를 확인한 후, 방문처리한다.

    todo :
     1. SpringSecurity 도입 후 principal로부터 ID 획득
     2. 로그인시 VisitedHeirtageSet 캐쉬에 저장하여 관리하도록 설정
     */

  public HeritageDto visitHeritage(String memberId, String heritageId) {

    log.info("visitHeritage Service start");

    boolean alreadyVisited = visitedHeritageRepository.existsByMemberEntity_MemberIdAndHeritageEntity_HeritageId(
        memberId,
        heritageId);

    if (alreadyVisited) {
      throw new CustomExcpetion(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
          "이미 방문처리한 유적입니다");
    }

    HeritageEntity heritageEntity = heritageRepository
        .findByHeritageId(heritageId)
        .orElseThrow(
            () -> new CustomExcpetion(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
                "존재하지 않는 유적ID에 대한 요청입니다."));

    MemberEntity memberEntity = memberRepository.findByMemberId(memberId)
        .orElseThrow(
            () -> new CustomExcpetion(ErrorCode.UNEXPECTED_REQUEST_FROM_FRONT,
                "존재하지 않는 유저ID에 대한 요청입니다."));

    VisitedHeritageEntity newVisit = VisitedHeritageEntity.builder()
        .memberEntity(memberEntity)
        .heritageEntity(heritageEntity)
        .build();

    visitedHeritageRepository.save(newVisit);

    log.info("visitHeritage Service finished");

    return HeritageDto.fromEntity(heritageEntity);
  }

  public List<HeritageDto> visitedHeritageByUser(String memberId) {

    log.info("visitedHeritageByUser Service start");

    List<HeritageEntity> visitedHeritages = visitedHeritageRepository.findAllVisitedHeritageByMemberId(
        memberId);

    return visitedHeritages.stream().map(HeritageDto::fromEntity).toList();
  }
}
