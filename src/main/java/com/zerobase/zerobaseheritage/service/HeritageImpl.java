package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeritageImpl {

  private final HeritageRepository heritageRepository;


  @Transactional
  public void insertOrUpdateHeritages(HeritageApiDto heritageApiDto) {
    log.info("heritage data init service start");

      heritageRepository.insertOrUpdate(
          heritageApiDto.getHeritageId(),
          heritageApiDto.getHeritageName(),
          heritageApiDto.getLocation()
          ,heritageApiDto.getHeritageGrade());

  }



  /*
  geolocationAdapter 을 사용하여 외부 Api Item 을 ApiDto 로 mapping 하여 DB에 저장할
  형태로 변환
   */



}



