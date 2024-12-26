package com.zerobase.zerobaseheritage.model.dto.RouteFind;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class BasePoint {
  private double longitudeX;
  private double latitudeY;
}
