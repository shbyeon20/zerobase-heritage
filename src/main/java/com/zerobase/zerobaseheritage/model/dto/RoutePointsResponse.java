package com.zerobase.zerobaseheritage.model.dto;

import com.zerobase.zerobaseheritage.model.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.model.dto.RouteFind.PointCollection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoutePointsResponse {
  long routeTime;
  List<BasePoint> routePoints;


  public static RoutePointsResponse fromPointCollection(
      PointCollection collection) {
    return RoutePointsResponse.builder()
        .routeTime(collection.getRouteTimeSum()+collection.getReturnTime())
        .routePoints(collection.getPoints())
        .build();
  }

}
