package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.entity.MapResponse;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.MapService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

  private final MapService mapService;
  private final GeoLocationAdapter geoLocationAdapter;


  @GetMapping("/create")
  public ResponseEntity<MapResponse> createMap(
      @RequestParam double northLatitude, @RequestParam double southLatitude,
      @RequestParam double eastLongitude,
      @RequestParam double westLongitude) {

    String userId = "user123";

    Polygon polygon = geoLocationAdapter.boxToPolygon(northLatitude,
        southLatitude, eastLongitude, westLongitude);

    MapResponse mapResponse = mapService.mapResponseWithGridsAndHeritages(
        userId, polygon, northLatitude,
        southLatitude, eastLongitude, westLongitude);

    return ResponseEntity.ok(mapResponse);
  }

}
