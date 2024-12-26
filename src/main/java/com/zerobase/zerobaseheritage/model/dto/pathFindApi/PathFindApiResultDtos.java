package com.zerobase.zerobaseheritage.model.dto.pathFindApi;

import com.zerobase.zerobaseheritage.model.dto.RouteFind.HeritagePoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PathFindApiResultDtos {

  private HeritagePoint nextDestinationCandidate;
  private PathFindApiResultDto pathToHeritageCandidate;
  private PathFindApiResultDto pathToReturn;

}