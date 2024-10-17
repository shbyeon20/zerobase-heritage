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

   basic description 에 대한 호출 도입하기

   */
  @GetMapping(value = "/external-data/heritage")
  public String loadHeritageData() {

    log.info("load heritage Data controller  start");

    initDataService.loadHeritageData();

    return "Data initialization completed";

  }
}


