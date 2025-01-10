package com.zerobase.zerobaseheritage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGrid {

  public double northLatitude;
  public double southLatitude;
  public double westLongitude;
  public double eastLongitude;

  private boolean isBlack = true;

  public MapGrid(double gridPoint_Longitude, double gridPoint_Latitude, double gridMinSize) {
    this.northLatitude = Math.round((gridPoint_Latitude) * 100) / 100.0;
    this.southLatitude = Math.round((gridPoint_Latitude + gridMinSize) * 100) / 100.0;
    this.westLongitude = Math.round((gridPoint_Longitude) * 100) / 100.0;
    this.eastLongitude = Math.round((gridPoint_Longitude + gridMinSize) * 100) / 100.0;
  }

  public static void unBlackGridIfPointExistInGrid(MapGrid grid, double longitude, double latitude) {
    if(longitude>grid.westLongitude&&longitude<grid.eastLongitude) {
      if(latitude>grid.southLatitude&&latitude<grid.northLatitude) {
        grid.isBlack = true;
      }
    }


  }
}
