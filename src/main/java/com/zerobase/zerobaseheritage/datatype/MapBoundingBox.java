package com.zerobase.zerobaseheritage.datatype;

public class MapBoundingBox {

  public double northLatitude;
  public double southLatitude;
  public double eastLongitude;
  public double westLongitude;

  public MapBoundingBox(
      double northLatitude, double southLatitude,
      double eastLongitude, double westLongitude) {

    // boundingbox를 위도 경도 0.01단위로 고정을 위해
    // North,east는 0.01단위로 올림
    // South,West는 0.01단위로 내림

    this.northLatitude = Math.ceil(northLatitude * 100) / 100.0;
    this.southLatitude = Math.floor(southLatitude * 100) / 100.0;
    this.eastLongitude = Math.ceil(eastLongitude * 100) / 100.0;
    this.westLongitude = Math.floor(westLongitude * 100) / 100.0;


  }
}
