package com.zerobase.zerobaseheritage.model;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapBoundingBox {

  public static final double GRID_SIZE = 0.01;

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


/*
  client 로부터 지도표시를 위해 위도의 최대최소, 경도의 최대최소, 4개 값을 받음
  grid 를 위도 경도 0.01도 단위로 규격화하여 boundingBox 를 생성
  boundingBox 를 gridMinSize 로 쪼개어 gridBox 로 만들고 List 에 추가함
   */

  public static List<MapGrid> createGridsFromBoundingBox(MapBoundingBox boundingBox) {


    List<MapGrid> grids = new ArrayList<>();
    // boundingBox 로 생성할수있는 grid 의 가로 세로 개수 계산
    int numOfGridsByLongitudeInBox = MapBoundingBox.divideLongitudeOfBoxByGridSize(boundingBox);
    int numOfGridsByLatitudeInBox = MapBoundingBox.divideLatitudeOfBoxByGridSize(boundingBox);


    // grid를 좌상단부터 우측으로 하나씩 순회
    for (int gridLat = 0; gridLat < numOfGridsByLatitudeInBox; gridLat++) {
      for (int gridLong = 0; gridLong < numOfGridsByLongitudeInBox; gridLong++) {

        // 각 grid의 좌상단 point를 기준으로 grid 객체 생성하여 리스트에 저장, double 의 floating number 끝자리 오류로 round 처리
        double pointLongitudeOfGrid = Math.round((boundingBox.westLongitude + GRID_SIZE * gridLong) * 100) / 100.0;
        double pointLatitudeOfGrid = Math.round((boundingBox.northLatitude - GRID_SIZE * gridLat) * 100) / 100.0;

        grids.add(new MapGrid(pointLongitudeOfGrid, pointLatitudeOfGrid, GRID_SIZE));

      }
    }
    return grids;

  }


  public static int divideLongitudeOfBoxByGridSize(MapBoundingBox boundingBox) {
    return (int) Math.round(((boundingBox.northLatitude - boundingBox.southLatitude)/ GRID_SIZE));
  }

  // bounding box 내에 grid 세로 개수 산출
  public static int divideLatitudeOfBoxByGridSize(MapBoundingBox boundingBox) {
    return (int) Math.round(((boundingBox.eastLongitude - boundingBox.westLongitude) / GRID_SIZE));
  }

}
