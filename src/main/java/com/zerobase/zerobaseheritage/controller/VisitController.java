package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VisitController {

  private final VisitService visitService;


  /*
  유저의 요청에 따라 방문처리한다.

   */
  @PostMapping("/visit")
  public String visitHeritage(@RequestParam String userId,
      @RequestParam String heritageId) {
    HeritageDto heritageDto = visitService.visitHeritage(userId, heritageId);

    return heritageDto.getHeritageName() + ": 방문처리완료";
  }


}
