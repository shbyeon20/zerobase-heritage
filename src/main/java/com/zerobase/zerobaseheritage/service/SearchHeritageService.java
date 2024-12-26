package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.model.exception.CustomException;
import com.zerobase.zerobaseheritage.model.exception.ErrorCode;
import com.zerobase.zerobaseheritage.model.dto.HeritageResponseDto;
import com.zerobase.zerobaseheritage.model.entity.HeritageEntity;
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
public class SearchHeritageService {

  private final HeritageRepository heritageRepository;
  private final GeoLocationAdapter geoLocationAdapter;

  private static final int SETTING_DISTANCE_METER = 5000;

  private static final double MIN_LATITUDE = 33.1147;
  private static final double MAX_LATITUDE = 38.6124;
  private static final double MIN_LONGITUDE = 124.6094;
  private static final double MAX_LONGITUDE = 131.8649;


  public List<HeritageResponseDto> ConvertToPointAndFindDistancedFrom(double longitude, double latitude) {
    log.info("search by location point service start for {},{}",longitude,latitude);

    validate(longitude,latitude);

    Point point = geoLocationAdapter.coordinateToPoint(longitude, latitude);


    List<HeritageEntity> heritageEntities
        = heritageRepository.findWithinDistance(point, SETTING_DISTANCE_METER);

    log.info("search by location point service finished ");
    return heritageEntities.stream().map(HeritageResponseDto::fromEntity).toList();
  }



  private static void validate(double longitude, double latitude) {
    // Validate the latitude and longitude
    if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE ||
        longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
      throw new CustomException(
          ErrorCode.LOCATION_OUT_OF_BOUND, "국내 위치에서만 검색가능합니다");
    }
  }


  public List<HeritageResponseDto> byPolygon(Polygon polygon) {
    log.info("search heritage by polygon service start ");

    List<HeritageEntity> heritages = heritageRepository.findWithinPolygon(
        polygon);

    log.info("search heritage by polygon service finished ");

    return heritages.stream().map(HeritageResponseDto::fromEntity).toList();

  }
}
