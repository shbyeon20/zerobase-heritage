package com.zerobase.zerobaseheritage.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapBoundingBox {

  public double northLatitude;
  public double southLatitude;
  public double eastLongitude;
  public double westLongitude;

  public MapBoundingBox(
      double northLatitude, double southLatitude,
      double eastLongitude, double westLongitude) {

    // boundingBox 를 위도 경도 0.01단위로 고정을 위해
    // North,East 는 0.01단위로 올림
    // South,West 는 0.01단위로 내림

    log.info("MapBoundingBox created with latitude: northLatitude={}, southLatitude={}", northLatitude, southLatitude);
    log.info("MapBoundingBox created with longitude: eastLongitude={}, westLongitude={}", eastLongitude, westLongitude);


    this.northLatitude = Math.ceil(northLatitude * 100) / 100.0;

    log.info("Rounded up northLatitude: {}", this.northLatitude);

    this.southLatitude = Math.floor(southLatitude * 100) / 100.0;

    log.info("Rounded down southLatitude: {}", this.southLatitude);

    this.eastLongitude = Math.ceil(eastLongitude * 100) / 100.0;

    log.info("Rounded up eastLongitude: {}", this.eastLongitude);

    this.westLongitude = Math.floor(westLongitude * 100) / 100.0;

    log.info("Rounded down westLongitude: {}", this.westLongitude);
    log.info("mapBoundingBox finished");


  }
}
