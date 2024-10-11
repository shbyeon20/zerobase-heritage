package com.zerobase.zerobaseheritage.externalApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.RouteFind.BasePoint;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResult;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PathFindApi {

  private final ObjectMapper objectMapper;


  @Value("${externalAPI.naver.client-Id}")
  private String CLIENT_ID;

  @Value("${externalAPI.naver.client-Secret}")
  private String CLIENT_SECRET_KEY;


  /*
      시작점과 도착점을 지정하면 외부 API로부터 경로 및 시간정보를 받아온다.
   */
  public PathFindApiResultDto getPathInfoBetweenPoints
  (BasePoint start, BasePoint end) {
    log.info("getPathInfoBetweenPoints start: {}, end: {}", start, end);

    WebClient webClient = WebClient.create(
        "https://naveropenapi.apigw.ntruss.com");

    String jsonResponse = webClient.get().uri
            (uriBuilder ->
                uriBuilder.path("/map-direction/v1/driving")
                    .queryParam("goal",
                        end.getLongitudeX() + "," + end.getLatitudeY())
                    .queryParam("start",
                        start.getLongitudeX() + "," + start.getLatitudeY())
                    .build())
        .header("x-ncp-apigw-api-key-id", CLIENT_ID)
        .header("x-ncp-apigw-api-key", CLIENT_SECRET_KEY)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    try {
      PathFindApiResult pathFindApiResult = objectMapper.readValue(jsonResponse,
          PathFindApiResult.class);

      return PathFindApiResultDto.fromResult(pathFindApiResult);
    } catch (JsonProcessingException e) {
      throw new CustomExcpetion(ErrorCode.EXTERNALAPI_NOT_FOUND,
          "PathFind API Not Found");
    }catch (NullPointerException e) {
      log.warn("예외적인 API호출로 Json 응답형식이 예상과 다릅니다. 본 API 호출은 무시처리 됩니다. : "+e.getMessage());
      return null;
    }
  }


}
