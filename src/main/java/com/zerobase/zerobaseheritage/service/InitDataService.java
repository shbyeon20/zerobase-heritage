package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.HeritageApiDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InitDataService {

  private final HeritageRepository heritageRepository;


  /*

   basic decription은 별도의 다른 API 호출이 필요하며 해당 APi 호출시
   ccbaKdcd, ccbaAsno, ccbaCtcd는 다른 APi 호출을 위해 필요한 para값
   별도의 API 호출이 필요하며
    다른 APi 호출을 위해 필요한 para값
    todo : 1. baiscDescription을 위한 API호출 추가 2.속도 향상을 위해서 multithread 향후 추가

   */
  public void initHeritageData(List<HeritageApiDto> heritageApiDtos) {
    log.info("heirtage data init service start");

    List<HeritageEntity> heritageEntities =
        heritageApiDtos.stream().map(HeritageApiDto::toEntity).toList();

   heritageRepository.saveAll(heritageEntities);


  }
}
