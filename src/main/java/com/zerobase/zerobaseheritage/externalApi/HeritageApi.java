package com.zerobase.zerobaseheritage.externalApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zerobase.zerobaseheritage.datatype.exception.CustomException;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeritageApi {

  private final XmlMapper xmlMapper;

  /*
    1. https://www.khs.go.kr 의 xml 데이터를 javabean 으로 맵핑

    2. item내 같이 호출된 ccbaKdcd, ccbaAsno, ccbaCtcd는 다른 APi 호출을 위해 필요한 para값임
    -> basic description에 대한 API 호출을 위해서 향후 쓰일 것임.

   */

  public HeritageApiResult fetchApiData(int pageNum) {
    WebClient client = WebClient.create("https://www.khs.go.kr");
    log.info("start fetchApiData for HeritageApi");

    String xmlResponse = client.get()
        .uri(uriBuilder -> uriBuilder
            .path("/cha/SearchKindOpenapiList.do")
            .queryParam("pageUnit", 300)
            .queryParam("pageIndex", pageNum)
            .queryParam("ccbaCncl", "N")
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .block();  // Synchronous call, use `block()` in non-reactive code

    try {
      log.info("API fetching completed : parsing XML to HeritageApiResult after API response: ");
      return xmlMapper.readValue(xmlResponse, HeritageApiResult.class);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new CustomException(ErrorCode.EXTERNALAPI_NOT_FOUND,
          "Heritage API Not Found");
    }
  }

}
