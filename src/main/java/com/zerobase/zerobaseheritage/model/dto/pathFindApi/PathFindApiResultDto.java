package com.zerobase.zerobaseheritage.model.dto.pathFindApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathFindApiResultDto {


  public long duration;

  public static PathFindApiResultDto fromResult(PathFindApiResult pathFindApiResult) throws NullPointerException {

    return PathFindApiResultDto.builder()
        .duration(pathFindApiResult.getRoute().getTraoptimal().get(0)
            .getSummary().getDuration())
        .build();


  }
}
