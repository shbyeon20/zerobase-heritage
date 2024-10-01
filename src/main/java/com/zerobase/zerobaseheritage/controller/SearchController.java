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

  private static final double MIN_LATITUDE = 33.1147;
  private static final double MAX_LATITUDE = 38.6124;
  private static final double MIN_LONGITUDE = 124.6094;
  private static final double MAX_LONGITUDE = 131.8649;

  private final SearchService searchService;
  private final GeoLocationAdapter geoLocationAdapter;

  @GetMapping("/pointlocation")
  public ResponseEntity<List<HeritageDto>> ByPointLocation(
      @RequestParam Double latitude, @RequestParam Double longitude) {

    // Validate the latitude and longitude
    if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE ||
        longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
      throw new CustomExcpetion(
          ErrorCode.LOCATION_OUT_OF_BOUND, "국내 위치에서만 검색가능합니다");
    }

    List<HeritageDto> heritageDtos = searchService.byPointLocation(
        geoLocationAdapter.coordinateToPoint(latitude, longitude));

    return ResponseEntity.ok(heritageDtos);
  }

}
