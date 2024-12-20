package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageInitService {

    private final GeoLocationAdapter geoLocationAdapter;
    
    public HeritageApiDto mapHeritageApiItemToApiDto(HeritageApiItem item) {
        return HeritageApiDto.builder()
            .heritageId(item.getHeritageId())
            .heritageName(item.getHeritageName())
            .location(this.convertCoordinateToPoint(item.getLongitude(), item.getLatitude()))
            .heritageGrade(item.getHeritageGrade())
            .build();
    }
    
    public Point convertCoordinateToPoint(double longitude, double latitude) {
        return geoLocationAdapter.coordinateToPoint(longitude, latitude);
    }

}

