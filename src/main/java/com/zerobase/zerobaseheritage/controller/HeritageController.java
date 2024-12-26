package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.model.dto.HeritageResponseDto;
import com.zerobase.zerobaseheritage.service.SearchHeritageService;
import com.zerobase.zerobaseheritage.service.VisitHeritageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/*
다양한 기준으로 문화재를 검색하는 기능을 제공한다
1. 유저의 위치 -> 주변 반경 5km의 문화재 리스트
 */
@Slf4j
@RestController()
@RequestMapping(value = "/api/heritages")
@RequiredArgsConstructor
public class HeritageController {

  private final SearchHeritageService searchHeritageService;
  private final VisitHeritageService visitHeritageService;

  /*
   특정 좌표 근처에 있는 유적지를 검색한다 ;
   */
  @GetMapping
  public ResponseEntity<List<HeritageResponseDto>> searchHeritageDistancedFromPoint(
      @RequestParam double search_from_latitude, @RequestParam double search_from_longitude) {
    log.info("HeritageController SearchHeritageFromPoint start ");

    List<HeritageResponseDto> heritageResponseDtoList = searchHeritageService.ConvertToPointAndFindDistancedFrom(
       search_from_longitude, search_from_latitude);


    return ResponseEntity.ok(heritageResponseDtoList);
  }


  /*
   유저의 요청에 따라 유적지를 방문처리한다.
   */
  @PostMapping("/user/{userId}/visited-heritage")
  public ResponseEntity<Object> visitHeritage(@PathVariable String userId,
      @RequestBody String heritageId) {
    log.info("HeritageController visit Heritage start");

    visitHeritageService.createVisit(userId, heritageId);

    return ResponseEntity.ok().build();
  }


}

