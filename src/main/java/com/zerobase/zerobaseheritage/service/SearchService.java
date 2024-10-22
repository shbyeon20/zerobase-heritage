package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private static final int DISTANCE_METER = 5000;
  private final HeritageRepository heritageRepository;
  private final GeoLocationAdapter geoLocationAdapter;


  private static final double MIN_LATITUDE = 33.1147;
  private static final double MAX_LATITUDE = 38.6124;
  private static final double MIN_LONGITUDE = 124.6094;
  private static final double MAX_LONGITUDE = 131.8649;


  public List<HeritageDto> byPointLocation(double longtitudeX, double latitudeY ) {
    log.info("search by location point service start for long : {} & lat :{}",longtitudeX,latitudeY);

    // Validate the latitude and longitude
    if (latitudeY < MIN_LATITUDE || latitudeY > MAX_LATITUDE ||
        longtitudeX < MIN_LONGITUDE || longtitudeX > MAX_LONGITUDE) {
      throw new CustomExcpetion(
          ErrorCode.LOCATION_OUT_OF_BOUND, "국내 위치에서만 검색가능합니다");
    }

    // point 객체로 변환
    Point point = geoLocationAdapter.coordinateToPoint(longtitudeX, latitudeY);

    List<HeritageEntity> heritageEntities
        = heritageRepository.findWithinDistance(point, DISTANCE_METER);

    log.info("search by location point service finished ");
    return heritageEntities.stream().map(HeritageDto::fromEntity).toList();
  }


  public List<HeritageDto> byPolygon(Polygon polygon) {
    log.info("search heritage by polygon service start ");

    List<HeritageEntity> heritages = heritageRepository.findWithinPolygon(
        polygon);

    log.info("search heritage by polygon service finished ");

    return heritages.stream().map(HeritageDto::fromEntity).toList();

  }
}
