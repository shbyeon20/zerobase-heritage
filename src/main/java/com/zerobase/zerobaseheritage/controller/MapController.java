package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.model.dto.MapResponse;
import com.zerobase.zerobaseheritage.service.MapFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/map")
public class MapController {

  private final MapFacadeService mapFacadeService;


  @GetMapping("")
  public ResponseEntity<MapResponse> getMap(@RequestParam String userId,
      @RequestParam double northLatitude, @RequestParam double southLatitude,
      @RequestParam double eastLongitude,
      @RequestParam double westLongitude) {



    MapResponse mapResponse = mapFacadeService.createMapResponseDtoWithColoredGridsAndHeritages(
        userId, northLatitude,
        southLatitude, eastLongitude, westLongitude);

    return ResponseEntity.ok(mapResponse);
  }

}
