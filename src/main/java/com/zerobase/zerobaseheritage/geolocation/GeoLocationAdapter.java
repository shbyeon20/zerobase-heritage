package com.zerobase.zerobaseheritage.geolocation;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeoLocationAdapter {
  private final GeometryFactory geometryFactory;


  /*
   jts 라이브러리를 활용하여 x,y 좌표를 point 객체로 변환.
   */
  public Point coordinateToPoint (Double latitude, Double longitude) {
    if (latitude == null || longitude == null) {
      log.error("value of longtitude : "+latitude+" or longtitude : "+longitude+" is null");
      throw new CustomExcpetion(ErrorCode.NullPointException,"external api return null value");
    }
    log.info("Creating Point with longitude={}, latitude={}", longitude, latitude);
    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    point.setSRID(4326);
    log.info("Creating Point with longitude={}, latitude={}", longitude, latitude);
    return point;
  }



}
