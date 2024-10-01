package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final int DISTANCE_METER = 5000;
  private final HeritageRepository heritageRepository;


  public List<HeritageDto> byPointLocation(Point point) {
    log.info("search by location point service start ");

    List<HeritageEntity> heritageEntities
        = heritageRepository.findWithinDistance(point, DISTANCE_METER)
        .filter(list -> !list.isEmpty()).orElseThrow(() ->
            new CustomExcpetion(ErrorCode.NO_HERITAGE_NEARBY, "주변에 존재하는 문화유적이 없습니다."));

    log.info("search by location point service finished ");
    return heritageEntities.stream().map(HeritageDto::fromEntity).toList();
  }
}
