package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiItem;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InitDataService {

  private final HeritageRepository heritageRepository;

  public void initHeritageData(List<HeritageApiItem> heritageApiItemList) {
    List<HeritageEntity> heritageEntities =
        heritageApiItemList.stream().map(HeritageApiDto::toEntiy).toList();

    heritageRepository.saveAll(heritageEntities);


  }


}
