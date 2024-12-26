package com.zerobase.zerobaseheritage.threading;

import com.zerobase.zerobaseheritage.model.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import com.zerobase.zerobaseheritage.service.HeritageImpl;
import com.zerobase.zerobaseheritage.service.HeritageInitService;
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

public class InitDataThread {

  @Qualifier("ExternalApiTaskExecutor")
  private final ThreadPoolTaskExecutor taskExecutor;
  private final HeritageApi heritageApi;
  private final HeritageImpl heritageImpl;
  private final HeritageInitService heritageInitService;

  /*
  외부 API 로부터 데이터를 1페이지 단위로 불러온 후에 저장한다
   */


  public Future<List<HeritageApiItem>> submitFetchAPIItemsAndSaveUnlessEmpty(int apiPageNumber) {

    return taskExecutor.submit(new Callable<List<HeritageApiItem>>() {

      @Override
      public List<HeritageApiItem> call() throws Exception {
        return heritageInitService.fetchAPIItemsAndSaveUnlessEmpty(apiPageNumber);
      }
    });
  }
}



