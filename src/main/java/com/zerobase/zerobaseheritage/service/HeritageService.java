package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiDto;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult.HeritageApiItem;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.geolocation.GeoLocationAdapter;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeritageService {

  private final HeritageRepository heritageRepository;
  private final GeoLocationAdapter geoLocationAdapter;


  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void saveHeritageDtos(List<HeritageApiDto> heritageApiDtos) {
    log.info("heritage data init service start");

    List<HeritageEntity> heritageEntities =
        heritageApiDtos.stream().map(HeritageApiDto::toEntity).toList();

    for (HeritageEntity heritageEntity : heritageEntities) {
      log.info("saveHeritageDto start : {}", heritageEntity.getHeritageName());
      heritageRepository.insertOrUpdate(
          heritageEntity.getHeritageId()
          , heritageEntity.getHeritageName()
          , heritageEntity.getLocation()
          , heritageEntity.getHeritageGrade()
          , heritageEntity.getBasicDescription());
    }
  }

  /*
  geolocationAdapter을 사용하여 외부 ApiItem을 ApiDto로 mapping하여
  저장할수 있는
   */
  public HeritageApiDto mapHeritageApiItemToApiDto(HeritageApiItem item) {
    return HeritageApiDto.builder()
        .heritageId(item.getHeritageId())
        .heritageName(item.getHeritageName())
        .location(geoLocationAdapter
            .coordinateToPoint(item.getLongitude(), item.getLatitude()))
        .heritageGrade(item.getHeritageGrade())
        .build();
  }


}



