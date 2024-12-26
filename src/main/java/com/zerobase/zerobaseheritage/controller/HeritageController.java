package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.model.dto.HeritageResponseDto;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.SearchService;
import com.zerobase.zerobaseheritage.service.VisitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/*
다양한 기준으로 문화재를 검색하는 기능을 제공한다
1. 유저의 위치 -> 주변 반경 5km의 문화재 리스트
 */
@Slf4j
@RestController()
@RequestMapping(value = "/heritage")
@RequiredArgsConstructor
public class HeritageController {

  private final SearchService searchService;
  private final GeoLocationAdapter geoLocationAdapter;
  private final VisitService visitService;

  /*
   특정 좌표 근처에 있는 유적지를 검색한다 ;
   */
  @GetMapping("/heritage-nearby-coordinate")
  public ResponseEntity<List<HeritageResponseDto>> ByPointLocation(
      @RequestParam Double latitude, @RequestParam Double longitude) {
    log.info("HeritageController find byLocation start ");

    List<HeritageResponseDto> heritageResponseDtoList = searchService.byPointLocation(
        geoLocationAdapter.coordinateToPoint(longitude, latitude));


    return ResponseEntity.ok(heritageResponseDtoList);
  }


  /*
   유저의 요청에 따라 유적지를 방문처리한다.
   */
  @PostMapping("/visited-heritage")
  public String visitHeritage(@RequestParam String userId,
      @RequestParam String heritageId) {
    log.info("HeritageController visit Heritage start");

    HeritageResponseDto heritageResponseDto = visitService.visitHeritage(userId, heritageId);

    return heritageResponseDto.getHeritageName() + ": 방문처리완료";
  }


}

