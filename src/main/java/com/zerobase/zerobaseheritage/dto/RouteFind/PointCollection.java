package com.zerobase.zerobaseheritage.dto.RouteFind;

import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointCollection {

  private LinkedList<BasePoint> points = new LinkedList<>();
  private int heritageGradePointSum;
  private long routeTimeSum;
  private long returnTime;



}
