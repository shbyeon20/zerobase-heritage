package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.model.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.model.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageInitService {

    private final GeoLocationAdapter geoLocationAdapter;
    private final HeritageApi heritageApi;
    private final HeritageImpl heritageImpl;


    public List<HeritageApiItem> fetchAPIItemsAndSaveUnlessEmpty(int apiPageNumber) {
        List<HeritageApiItem> heritageApiItems = heritageApi.fetchApiData(apiPageNumber).getHeritageApiItemList();

        if (heritageApiItems==null) {
            return null;
        }else{
            convertApiItemsAndSaveDtos(heritageApiItems);
            return heritageApiItems;
        }
    }

    @Transactional
    protected void convertApiItemsAndSaveDtos(
        List<HeritageApiItem> heritageApiItems) {
        for (HeritageApiItem heritageApiItem : heritageApiItems) {
            HeritageApiDto heritageApiDto = this.mapHeritageApiItemToApiDto(heritageApiItem);
            heritageImpl.insertOrUpdateHeritages(heritageApiDto);
        }
    }

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

