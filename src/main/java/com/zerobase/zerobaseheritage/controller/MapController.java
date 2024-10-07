package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.entity.MapResponse;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.MapService;
import com.zerobase.zerobaseheritage.service.SearchService;
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
  private final SearchService searchService;


  @GetMapping("/createmap")
  public ResponseEntity<MapResponse> createMap(
      @RequestParam double north_Latitude, @RequestParam double south_Latitude,
      @RequestParam double east_Longitude, @RequestParam double west_Longitude) {

    String userId = "user123";

    Polygon polygon = geoLocationAdapter.boxToPolygon(north_Latitude,
        south_Latitude, east_Longitude, west_Longitude);

    MapResponse mapResponse = mapService.mapResponseWithGridsAndHeritages(
        userId, polygon, north_Latitude,
        south_Latitude, east_Longitude, west_Longitude);

    return ResponseEntity.ok(mapResponse);
  }

}
