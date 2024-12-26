package com.zerobase.zerobaseheritage.model.dto.pathFindApi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathFindApiResult {

  private Route route;

  @Getter
  @Setter
  public static class Route {
    List<Traoptimal> traoptimal;


  }
  @Getter
  @Setter
  public static class Traoptimal {
    private Summary summary;

  }

  @Getter
  @Setter
  public static class Summary {
    private int distance;
    private long duration;

  }
}
