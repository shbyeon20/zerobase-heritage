package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.dto.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiItem;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.InitDataService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestInitController {

  private final HeritageApi heritageApi;
  private final InitDataService initDataService;
  private final GeoLocationAdapter geoLocationAdapter;


  /*
  1. 외부 API를 통해 xml -> javabean -> dto로 변환함. 호출은 300레코드씩 여러번 진행됨.
  todo : 1. 비동기 프로그래밍 후속 도입하기
   2. basic description에 대한 호출 도입하기
    3. native query로 batch save 향후 구현

  2. program eventlistner로 변형시키기전 test controller로 테스트
   */
  @GetMapping(value = "/test-heritage-api")
  public String testHeritageApi() {

    log.info("test controller for api data init start");

    List<HeritageApiDto> heritageApiDtoList = new ArrayList<>();
    int recordSavedCnt = 0;
    int PageNumber = 1;


    while (true) {
      // import external api by page and convert
      HeritageApiResult heritageApiResult = heritageApi.fetchApiData(PageNumber);
      List<HeritageApiItem> heritageApiItems = heritageApiResult.getHeritageApiItemList();

      // create dto from javabean
      for (HeritageApiItem item : heritageApiItems) {
        HeritageApiDto heritageApiDto = HeritageApiDto.builder()
            .heritageId(item.getHeritageId())
            .heritageName(item.getHeritageName())
            .location(geoLocationAdapter.coordinateToPoint( item.getLongitude(),item.getLatitude()))
            .heritageGrade(item.getHeritageGrade())
            .build();

        //add it into list
        heritageApiDtoList.add(heritageApiDto);
      }
      // move dto list to service layer
      recordSavedCnt += initDataService.initHeritageData(heritageApiDtoList);
      log.info("recordCnt=" + recordSavedCnt);

      if (recordSavedCnt >= heritageApiResult.getTotalCnt()) { // DB에 저장된 Record와 외부API의 전체 Record수가 일치하면 break
        log.info("external api data loading finished");
        break;
      }
      PageNumber += 1;
    }

    return "Data initialization completed, recordSavedCnt : " + recordSavedCnt;

  }
}


