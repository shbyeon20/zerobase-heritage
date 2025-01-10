package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.threading.InitDataThread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageInitManagementService {


    private final InitDataThread initDataThread;

  /*
  외부 API로부터 300 record x 1 page x 10회 callable 단위로 데이터 로드 후 저장하는 작업을 수행
  외부 API 데이터가 비어있으면 while문을 중단한다.
   */

    public void loadHeritageData()
        throws ExecutionException, InterruptedException {
        log.info("heritage data init service start");

        int apiPageNumber = 1;
        List<Future<List<HeritageApiItem>>> futures = new ArrayList<>();

        boolean isEmpty = false;
        while (!isEmpty) {
            for (int i = 0; i < 10; i++) {
                futures.add(initDataThread.submitFetchAPIItemsAndSaveUnlessEmpty(apiPageNumber));
                apiPageNumber += 1; // api의 다음 페이지 선택
                Thread.sleep(100); // 외부 API 부하경감을 위한 Sleep
            }
            // 10회 API 호출 및 저장을 수행하고 API 데이터가 비어있는지 확인한다

            for (int i = 0; i < 10; i++) {
                List<HeritageApiItem> heritageApiItems = futures.get(i).get();
                if (heritageApiItems == null || heritageApiItems.isEmpty())
                // 외부 API 데이터가 비어있으면 loop를 중단
                {
                    isEmpty = true;
                }
            }
            futures.clear();
        }
    }


}
