package com.zerobase.zerobaseheritage.dto;

import com.zerobase.zerobaseheritage.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.dto.pathFindApi.PathFindApiResultDto;
import java.util.ArrayList;

public class RouteDto {

  public CustomPoint StartPoint;

  public ArrayList<PathFindApiResultDto> Paths;

  public CustomPoint endPoint;

}
