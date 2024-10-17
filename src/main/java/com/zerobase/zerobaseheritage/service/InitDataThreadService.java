package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
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

@Service
@Slf4j
@RequiredArgsConstructor

public class InitDataThreadService {

  @Qualifier("ExternalApiTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;
  private final HeritageApi heritageApi;
  private final HeritageService heritageService;

  public Future<List<HeritageApiItem>> submitLoadHeritageApiDataAndSave(int apiPageNumber) {

    log.info(
        "InitDataThreadService Submitting task for page : {} Active Threads: {}, Queue Size: {}",
        apiPageNumber,
        taskExecutor.getActiveCount(),
        taskExecutor.getThreadPoolExecutor().getQueue().size());


    return taskExecutor.submit(new Callable<List<HeritageApiItem>>() {


      @Override
      public List<HeritageApiItem> call() throws Exception {

        List<HeritageApiDto> heritageApiDtos = new ArrayList<>();
        // import external api by page
        HeritageApiResult heritageApiResult = heritageApi.fetchApiData(
            apiPageNumber);
        List<HeritageApiItem> heritageApiItems = heritageApiResult.getHeritageApiItemList();


        if (heritageApiItems == null) {
          return null;
        }

        // create dto from Api Item and add to container
        for (HeritageApiItem item : heritageApiItems) {
          HeritageApiDto heritageApiDto = heritageService.mapHeritageApiItemToApiDto(
              item);
          heritageApiDtos.add(heritageApiDto);

        }
        heritageService.saveHeritageDtos(heritageApiDtos);
        return heritageApiItems;

      }
    });
  }
}
