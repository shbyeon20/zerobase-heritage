package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.service.InitDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InitHeritageDataController {

  public final InitDataService initDataService;

  /*
   외부 API 통해서 데이터를 내부 DB로 저장한다
   todo : basic description 에 대한 호출 도입하기
   */
  @GetMapping(value = "/external-data/heritage")
  public String loadHeritageData() {

    log.info("load heritage Data controller  start");

    initDataService.loadHeritageData();

    return "Data initialization completed";

  }
}


