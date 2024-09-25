package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
import com.zerobase.zerobaseheritage.externalApi.HeritageApi;
import com.zerobase.zerobaseheritage.service.InitDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class testController {

  private final HeritageApi heritageApi;
  private final InitDataService initDataService;

  @GetMapping("/test-heritage-api")
  public HeritageApiResult testHeritageApi() {
    HeritageApiResult heritageApiResult = heritageApi.importAPI();

    initDataService.initHeritageData(heritageApiResult.getHeritageApiItemList());
    return heritageApiResult;
  }
}
