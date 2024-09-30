package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final HeritageRepository heritageRepository;

  public List<HeritageDto> byPointLocation(Point point){

    List<HeritageDto> heritageDtos = null;

    return heritageDtos;
  }
}
