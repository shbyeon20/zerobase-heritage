package com.zerobase.zerobaseheritage.dto.RouteFind;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathCollection {

  private BasePoint startPoint;
  private BasePoint endPoint;

  private List<Path> Paths;


  private int durationSum;
  private int heritageGradePointSum;



}
