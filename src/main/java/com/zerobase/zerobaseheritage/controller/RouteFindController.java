package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.dto.RoutePointsResponse;
import com.zerobase.zerobaseheritage.service.RouteFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RouteFindController {

  private final RouteFindService routeFindService;

  @GetMapping("/route/get")
  public ResponseEntity<RoutePointsResponse> routeFind
      (@RequestParam double latitude,
          @RequestParam double longitude,
          @RequestParam long timeLimit) {

    log.info("routeFind Controller start for latitude={}, longitude={}, timeLimit={}", latitude, longitude, timeLimit);

    CustomPoint clientPoint = CustomPoint.builder().latitudeY(latitude)
        .longitudeX(longitude).build();


    return ResponseEntity.ok(
        routeFindService.routeFind(clientPoint, timeLimit));

  }

}
