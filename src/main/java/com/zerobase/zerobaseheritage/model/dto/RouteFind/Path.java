package com.zerobase.zerobaseheritage.model.dto.RouteFind;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Path {

  private BasePoint startPoint;
  private BasePoint endPoint;
  private String time;

  private String pathDescription;


}

