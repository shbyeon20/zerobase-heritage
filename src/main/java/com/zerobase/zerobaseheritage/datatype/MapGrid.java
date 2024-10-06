package com.zerobase.zerobaseheritage.datatype;

import static com.zerobase.zerobaseheritage.service.MapService.gridMinSize;

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

  public MapGrid(double gridPoint_Longitude, double gridPoint_Latitude) {
    this.north_Latitude = gridPoint_Latitude;
    this.south_Latitude = gridPoint_Latitude + gridMinSize;
    this.west_Longitude = gridPoint_Longitude;
    this.east_Longitude = gridPoint_Longitude + gridMinSize;
  }
}
