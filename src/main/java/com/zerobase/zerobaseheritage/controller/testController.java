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
import org.locationtech.jts.geom.Point;
import org.springframework.http.MediaType;
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

  2. program eventlistner로 변형시키기전 test controller로 테스트
   */
  @GetMapping(value = "/test-heritage-api")
  public String testHeritageApi() {

    List<HeritageApiDto> heritageApiDtoList = new ArrayList<>();
    int recordCnt =0;

    for (int i = 1; i <= 52; i++) {

      HeritageApiResult heritageApiResult = heritageApi.importAPI(i);
      List<HeritageApiItem> heritageApiItems = heritageApiResult.getHeritageApiItemList();

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
              "heritageApiDto.getLocation() is nul");
        }

        heritageApiDtoList.add(heritageApiDto);
      }


      initDataService.initHeritageData(heritageApiDtoList);
      recordCnt+=heritageApiDtoList.size();
      log.info("recordCnt="+recordCnt);
    }

    return "Data initialization completed, recordCnt : "+ recordCnt;

  }
}

