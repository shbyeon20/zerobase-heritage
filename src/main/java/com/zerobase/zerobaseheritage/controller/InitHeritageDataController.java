package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.service.HeritageInitManagementService;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/initialization")

public class InitHeritageDataController {

  public final HeritageInitManagementService heritageInitManagementService;

  /*
   외부 API 통해서 데이터를 내부 DB로 저장한다
   todo : basic description 에 대한 호출 도입하기
   */
  @PostMapping(value = "/heritages")
  public ResponseEntity<Void> loadHeritageData()
      throws ExecutionException, InterruptedException {

    log.info("load heritage Data controller  start");

    heritageInitManagementService.loadHeritageData();

    return ResponseEntity.ok().build();

  }
}


