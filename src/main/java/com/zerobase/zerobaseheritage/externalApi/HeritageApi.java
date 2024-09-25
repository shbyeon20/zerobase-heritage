package com.zerobase.zerobaseheritage.externalApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.heritageApi.HeritageApiResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeritageApi {

  private final XmlMapper xmlMapper;

  public HeritageApiResult importAPI() {
    WebClient client = WebClient.create("https://www.khs.go.kr");
    log.info("HeritageApi importAPI");

    String xmlResponse = client.get()
        .uri("/cha/SearchKindOpenapiList.do")
        .retrieve()
        .bodyToMono(String.class)
        .block();  // Synchronous call, use `block()` in non-reactive code

    try {
      log.info("Successfully parsed XML to HeritageApiResult");
      return xmlMapper.readValue(xmlResponse, HeritageApiResult.class);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new CustomExcpetion(ErrorCode.EXTERNALAPI_NOT_FOUND, "Heritage API Not Found");
    }
  }

}
