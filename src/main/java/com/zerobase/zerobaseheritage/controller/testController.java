package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
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
public class testController {

  private final HeritageApi heritageApi;
  private final InitDataService initDataService;
  private final GeoLocationAdapter geoLocationAdapter;


  /*
  1. 외부 API를 통해 xml -> javabean -> dto로 변환함. 호출은 300레코드씩 여러번 진행됨.
  todo : 1. 비동기 프로그래밍 후속 도입하기 2. basic description에 대한 호출 도입하기

  2. program eventlistner로 변형시키기전 test controller로 테스트
   */
  @GetMapping(value = "/test-heritage-api")
  public String testHeritageApi() {

    log.info("test controller for api data init start");

    List<HeritageApiDto> heritageApiDtoList = new ArrayList<>();
    int recordSavedCnt =0;
    int endOfPage = 52;
    int startOfPage = 1;


    for (int pageNum = startOfPage; pageNum <= endOfPage; pageNum++) {
      // import external api by page and convert
      HeritageApiResult heritageApiResult = heritageApi.importAPI(pageNum);
      List<HeritageApiItem> heritageApiItems = heritageApiResult.getHeritageApiItemList();

      // create dto from javabean
      for (HeritageApiItem item : heritageApiItems) {
        HeritageApiDto heritageApiDto = HeritageApiDto.builder()
            .heritageId(item.getHeritageId())
            .heritageName(item.getHeritageName())
            .location(geoLocationAdapter.coordinateToPoint(item.getLatitude(), item.getLongitude()))
            .heritageGrade(item.getHeritageGrade())
            .build();

        if (heritageApiDto.getLocation() == null) {
          log.error("heritageApiDto.getLocation() is null");
          throw new CustomExcpetion(ErrorCode.NullPointException,
              "heritageApiDto.getLocation() is null");
        }
      //add it into list
        heritageApiDtoList.add(heritageApiDto);
      }
      // move dto list to service layer
      initDataService.initHeritageData(heritageApiDtoList);
      recordSavedCnt+=heritageApiDtoList.size();
      log.info("recordCnt="+recordSavedCnt);
    }

    return "Data initialization completed, recordSavedCnt : "+ recordSavedCnt;

  }
}

