package com.zerobase.zerobaseheritage.datatype;

import static com.zerobase.zerobaseheritage.service.MapService.gridMinSize;

public class MapBoundingBox {

  public double north_Latitude;
  public double south_Latitude;
  public double east_Longitude;
  public double west_Longitude;

  public int numOfGrid_Longitude;
  public int numOfGrid_Latitude;


  public MapBoundingBox(
      double north_Latitude, double south_Latitude,
      double east_Longitude, double west_Longitude) {

    // boundingbox를 위도 경도 0.01단위로 고정을 위해
    // North,east는 0.01단위로 올림
    // South,West는 0.01단위로 내림

    this.north_Latitude = Math.ceil(north_Latitude * 100) / 100.0;
    this.south_Latitude = Math.floor(south_Latitude * 100) / 100.0;
    this.east_Longitude = Math.ceil(east_Longitude * 100) / 100.0;
    this.west_Longitude = Math.floor(west_Longitude * 100) / 100.0;

    // bounding box 내에 grid 개수 산출

    this.numOfGrid_Longitude =
        (int) ((north_Latitude - south_Latitude) / gridMinSize);
    this.numOfGrid_Latitude =
        (int) ((east_Longitude - west_Longitude) / gridMinSize);
  }
}
