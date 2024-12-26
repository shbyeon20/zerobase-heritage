package com.zerobase.zerobaseheritage.geolocation;

import com.zerobase.zerobaseheritage.model.exception.CustomException;
import com.zerobase.zerobaseheritage.model.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeoLocationAdapter {

  private final GeometryFactory geometryFactory;


  /*
   jts 라이브러리를 활용하여 x,y 좌표를 point 객체로 변환.
   */
  public Point coordinateToPoint(Double longitude, Double latitude) {
    if (latitude == null || longitude == null) {
      log.error(
          "value of longtitude : " + latitude + " or longtitude : " + longitude
              + " is null");
      throw new CustomException(ErrorCode.NULL_POINT_EXCEPTION,
          "external api return null value");
    }

    Point point = geometryFactory.createPoint(
        new Coordinate(longitude, latitude));

    if (point == null) {
      log.error("point instance is null");
      throw new CustomException(ErrorCode.NULL_POINT_EXCEPTION,
          "point instance is null");
    }
    point.setSRID(4326);
    return point;
  }


  public Polygon boxToPolygon(
      double northLatitude, double southLatitude,
      double eastLongitude, double westLongitude) {

    Coordinate[] coordinates = new Coordinate[]{
        new Coordinate(eastLongitude, southLatitude),
        new Coordinate(eastLongitude, northLatitude),
        new Coordinate(westLongitude, southLatitude),
        new Coordinate(westLongitude, northLatitude),
        new Coordinate(eastLongitude, southLatitude)
    };

    return geometryFactory.createPolygon(coordinates);
  }


}
