package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/*
다양한 기준으로 문화재를 검색하는 기능을 제공한다
1. 유저의 위치 -> 주변 반경 5km의 문화재 리스트
 */
@Slf4j
@RestController()
@RequestMapping(value = "/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;
  private final GeoLocationAdapter geoLocationAdapter;

  @GetMapping("/pointlocation")
  public ResponseEntity<List<HeritageDto>> ByPointLocation(
      @RequestParam Double latitude, @RequestParam Double longitude) {

    List<HeritageDto> heritageDtos = searchService.byPointLocation(
        geoLocationAdapter.coordinateToPoint(latitude, longitude));

    return ResponseEntity.ok(heritageDtos);
  }

}
