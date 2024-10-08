package com.zerobase.zerobaseheritage.datatype;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGrid {

  public double north_Latitude;
  public double south_Latitude;
  public double west_Longitude;
  public double east_Longitude;

  private boolean isBlack = true;

  public MapGrid(double gridPoint_Longitude, double gridPoint_Latitude,
      double gridMinSize) {
    this.north_Latitude = Math.round((gridPoint_Latitude) * 100) / 100.0;
    this.south_Latitude = Math.round((gridPoint_Latitude + gridMinSize) * 100) / 100.0;
    this.west_Longitude = Math.round((gridPoint_Longitude) * 100) / 100.0;
    this.east_Longitude = Math.round((gridPoint_Longitude + gridMinSize) * 100) / 100.0;
  }
}
