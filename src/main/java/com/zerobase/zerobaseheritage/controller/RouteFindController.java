package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.service.RouteFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/route")
public class RouteFindController {

  private final RouteFindService routeFindService;

  @GetMapping()
  public ResponseEntity<RoutePointsResponse> routeFind
      (   @RequestParam String userId,
          @RequestParam double longitude,
          @RequestParam double latitude,
          @RequestParam long timeLimit) {

    log.info("routeFind Controller start for latitude={}, longitude={}, timeLimit={}", latitude, longitude, timeLimit);



    return ResponseEntity.ok(
        routeFindService.routeFind(userId,longitude, latitude, timeLimit));

  }

}
