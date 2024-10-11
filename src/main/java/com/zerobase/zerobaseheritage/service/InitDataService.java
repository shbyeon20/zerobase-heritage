package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
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

   controller에서 레코드 리스트를 받고, 리스트를 순회하며 insertignore을 통해서 반복저장함. 저장성공시마다  ++하여 최종값 return

   ->  외부 API상 중복된 데이터 다수 존재하여 SaveAll()로 저장시 중복된 데이터로 인해 exception발생함
   ->> insertIgnore로 방법 변경, 향후 복수의 레코드를 insertingore하는 방법 추가 예정

   basic decription은 별도의 다른 API 호출이 필요하며 해당 APi 호출시
   ccbaKdcd, ccbaAsno, ccbaCtcd의 para 값이 필요함, 향후 추가예정

    todo : 1. baiscDescription을 위한 API호출 추가 2.속도 향상을 위해서 multithread 향후 추가 3. insertignore batch처리할수 있도록 향후 추가

   */
  public int initHeritageData(List<HeritageApiDto> heritageApiDtos) {
    log.info("heritage data init service start");

    List<HeritageEntity> heritageEntities =
        heritageApiDtos.stream().map(HeritageApiDto::toEntity).toList();

    int recordCnt = 0;

    for (HeritageEntity heritageEntity : heritageEntities) {
      recordCnt += heritageRepository.insertIgnore(
          heritageEntity.getHeritageId()
          , heritageEntity.getHeritageName()
          , heritageEntity.getLocation()
          , heritageEntity.getHeritageGrade()
          , heritageEntity.getBasicDescription());
    }

    return recordCnt;

  }
}
