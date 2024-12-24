package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor

public class InitDataThreadService {

  @Qualifier("ExternalApiTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;
  private final HeritageApi heritageApi;
  private final HeritageImpl heritageImpl;
  private final HeritageInitService heritageInitService;

  /*
  외부 API 로부터 데이터를 1페이지 단위로 불러온 후에 저장한다
   */


  public Future<List<HeritageApiItem>> submitLoadHeritageApiDataAndSave(int apiPageNumber) {

    return taskExecutor.submit(new Callable<List<HeritageApiItem>>() {

      @Override
      public List<HeritageApiItem> call() throws Exception {

        List<HeritageApiDto> heritageApiDtos = new ArrayList<>();
        // import external api by page number
        List<HeritageApiItem> heritageApiItems = heritageApi.fetchApiData(apiPageNumber).getHeritageApiItemList();

        if (heritageApiItems == null) {
          return null;
        }

        // create dto from Api Item and add to container for saving to repo
        for (HeritageApiItem item : heritageApiItems) {
          HeritageApiDto heritageApiDto = heritageInitService.mapHeritageApiItemToApiDto(item);
          heritageApiDtos.add(heritageApiDto);
        }

        heritageImpl.insertOrUpdateHeritages(heritageApiDtos);
        return heritageApiItems;

      }
    });
  }
}



