package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisCacheService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final SearchService searchService;
  private final GeoLocationAdapter geoLocationAdapter;
  private static final int DISTANCE_METER = 5000;

  // Gyeongju Boundaries (North, South, East, West)
  public static final double GYEONGJU_NORTH_LAT = 36.2;
  public static final double GYEONGJU_SOUTH_LAT = 35.8;
  public static final double GYEONGJU_EAST_LON = 129.5;
  public static final double GYEONGJU_WEST_LON = 129.0;

  // Seoul Boundaries (North, South, East, West)
  public static final double SEOUL_NORTH_LAT = 37.7;
  public static final double SEOUL_SOUTH_LAT = 37.4;
  public static final double SEOUL_EAST_LON = 127.2;
  public static final double SEOUL_WEST_LON = 126.8;

   /*
  서버동작시 서울과 경주의 데이터 로딩진행

  1. Gyeongju (경주):
   Latitude: Approximately 35.8° to 36.2° N
    Longitude: Approximately 129.0° to 129.5° E
  2. Seoul (서울):
  Latitude: Approximately 37.4° to 37.7° N,
  Longitude: Approximately 126.8° to 127.2° E
   */

  @PostConstruct
  public void preloadHeritageEntityData() {
    List<HeritageDto> gyeonjuDtos = searchService.byPolygon(
        geoLocationAdapter.boxToPolygon(GYEONGJU_NORTH_LAT, GYEONGJU_SOUTH_LAT,
            GYEONGJU_EAST_LON, GYEONGJU_WEST_LON));

    for (HeritageDto gyeonjuDto : gyeonjuDtos) {
      this.cacheHeritageEntity(gyeonjuDto);
    }

    List<HeritageDto> seoulDtos = searchService.byPolygon(
        geoLocationAdapter.boxToPolygon(SEOUL_NORTH_LAT, SEOUL_SOUTH_LAT,
            SEOUL_EAST_LON, SEOUL_WEST_LON));

    for (HeritageDto seoulDto : seoulDtos) {
      this.cacheHeritageEntity(seoulDto);
    }
  }


  // heritage site를 commonKeys에 저장
  public void cacheHeritageEntity(HeritageDto heritageDto) {
    redisTemplate.opsForGeo()
        .add("commonKeys",
            new Point(heritageDto.getLongitude(), heritageDto.getLatitude()),
            heritageDto);
  }

  // 좌표가 특정 지역 내에 존재한다면 캐쉬에서 findHeritageWithinDistance 실행하기.
  public GeoResults<GeoLocation<Object>> findHeritagesWithinDistance(
      double longitude, double latitude) {
    return redisTemplate.opsForGeo().radius("commonKeys",
        new Circle(new Point(longitude, latitude),
            new Distance(DISTANCE_METER, Metrics.METERS)));
  }


}
