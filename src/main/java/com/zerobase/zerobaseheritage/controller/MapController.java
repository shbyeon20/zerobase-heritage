package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.datatype.MapGrid;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.MapResponse;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.service.MapService;
import com.zerobase.zerobaseheritage.service.SearchService;
import java.util.List;
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


  @GetMapping("/createGrid")
  public ResponseEntity<MapResponse> createGridColored(
      @RequestParam double north_Latitude, @RequestParam double south_Latitude,
      @RequestParam double east_Longitude, @RequestParam double west_Longitude) {

    String userId = "user123";

    Polygon polygon = geoLocationAdapter.boxToPolygon(north_Latitude,
        south_Latitude, east_Longitude, west_Longitude);

    List<HeritageDto> heritagesInBox = searchService.byPolygon(polygon);

    List<MapGrid> mapGrids = mapService.createGridsWithColor(
        north_Latitude, south_Latitude, east_Longitude, west_Longitude, userId);


    return ResponseEntity.ok(
        MapResponse.builder()
            .heritagesInBox(heritagesInBox)
            .mapGrids(mapGrids)
            .build()
    );
  }

}
