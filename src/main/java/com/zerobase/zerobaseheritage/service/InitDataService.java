package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InitDataService {

  private final HeritageApi heritageApi;
  private final HeritageService heritageService;
  private final InitDataThreadService initDataThreadService;


  /*

   ->  외부 API 상 중복된 데이터 다수 존재하여 SaveAll()로 저장시 중복된 데이터로 인해 exception 발생하므로 insert on duplicate update로 변경
   (myIsam엔진 사용시 deadlock발생으로 innoDB 엔진으로 변경)

   basic description 은 별도의 다른 API 호출이 필요하며 해당 APi 호출시
   ccbaKdcd, ccbaAsno, ccbaCtcd의 para 값이 필요함

   */

  public void loadHeritageData() {
    log.info("heritage data init service start");

    // load initial data of external api
    int apiPageNumber = 1;
    List<HeritageApiItem> heritageApiItems = new LinkedList<>();

    //
    List<Future<List<HeritageApiItem>>> futures = new ArrayList<>();
    Boolean isEmpty = false;

    while (!isEmpty) {
      for (int i = 0; i < 10; i++) {
        log.info("current Api pageNumber : {}", apiPageNumber);

        futures.add(initDataThreadService.submitLoadHeritageApiDataAndSave(
            apiPageNumber));
        apiPageNumber += 1;

        try{
          Thread.sleep(100);
        }catch (Exception e) {
          throw new CustomExcpetion(ErrorCode.THREAD_EXCEPTION,"init data thread exception 발생");
        }

      }
      for (int i = 0; i < 10; i++) {
        try {
          heritageApiItems = futures.get(i).get();
          log.info("heritage api items in initdata service expecting it to be null at some point : {}", heritageApiItems);
        } catch (Exception e) {
          e.printStackTrace();
          throw new CustomExcpetion(ErrorCode.THREAD_EXCEPTION,
              "loadHeritageData thread exception");
        }
        if (heritageApiItems== null || heritageApiItems.size() == 0) {
          log.info("heritage api items is empty finishing the loop for fetchin api");
          isEmpty = true;
        }

      }
      futures.clear();

    }
    log.info("load heritage Data service  finished");
  }


}
