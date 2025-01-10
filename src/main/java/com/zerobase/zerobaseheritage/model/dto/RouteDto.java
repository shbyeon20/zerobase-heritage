package com.zerobase.zerobaseheritage.model.dto;

import com.zerobase.zerobaseheritage.model.dto.RouteFind.CustomPoint;
import com.zerobase.zerobaseheritage.model.dto.pathFindApi.PathFindApiResultDto;
import java.util.ArrayList;

public class RouteDto {

  public CustomPoint StartPoint;

  public ArrayList<PathFindApiResultDto> Paths;

  public CustomPoint endPoint;

}
