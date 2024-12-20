package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.datatype.exception.CustomException;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitDataService {

    private final InitDataThreadService initDataThreadService;




  /*

  외부 API로부터 300 record x 1 page x 10회 callable 단위로 데이터 로드 후 저장하는 작업을 수행
  외부 API 데이터가 비어있으면 while문을 중단한다.

  trouble
   ->  외부 API에 중복된 데이터 다수 존재하여 SaveAll()로 저장시 중복 exception 발생하므로 insert on duplicate update로 변경
   -->  MySQL myIsam엔진 사용시 Insert on duplicate update시 deadlock발생, sharedLock 원인으로 추정됨. innoDB 엔진으로 변경

   todo : basic description 은 별도의 다른 API 호출이 필요. 해당 API 호출시 ccbaKdcd, ccbaAsno, ccbaCtcd의 para 값이 필요함
   */

    @Transactional
    public void loadHeritageData() {
        log.info("heritage data init service start");

        int apiPageNumber = 1;
        List<HeritageApiItem> heritageApiItems;
        List<Future<List<HeritageApiItem>>> futures = new ArrayList<>();
        Boolean isEmpty = false;

        while (!isEmpty) {
            for (int i = 0; i < 10; i++) {
                log.info("current Api pageNumber : {}", apiPageNumber);
                futures.add(initDataThreadService.submitLoadHeritageApiDataAndSave(apiPageNumber));
                apiPageNumber += 1;
                try {
                    Thread.sleep(100); // 외부 API 부하경감을 위한 Sleep
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.THREAD_EXCEPTION,
                        "init data thread exception 발생");
                }
            }
            // 10회 API 호출 및 저장을 수행하고 API 데이터가 비어있는지 확인한다

            for (int i = 0; i < 10; i++) {
                try {
                    heritageApiItems = futures.get(i).get();
                    log.info(
                        "heritage api items in initdata service expecting it to be null at some point : {}",
                        heritageApiItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CustomException(ErrorCode.THREAD_EXCEPTION,
                        "loadHeritageData thread exception");
                }
                // 외부 API 데이터가 비어있으면 loop를 중단
                if (heritageApiItems == null || heritageApiItems.size() == 0) {
                    log.info(
                        "heritage api items is empty finishing the loop for fetchin api");
                    isEmpty = true;
                }

            }
            futures.clear();

        }
        log.info("load heritage Data service  finished");
    }


}

