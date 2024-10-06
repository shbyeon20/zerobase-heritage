package com.zerobase.zerobaseheritage.service;


import com.zerobase.zerobaseheritage.datatype.MapBoundingBox;
import com.zerobase.zerobaseheritage.datatype.MapGrid;
import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.entity.MemberEntity;
import com.zerobase.zerobaseheritage.repository.MemberRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/*
client로부터 지도표시를 위해 위도선의 최대최소, 경도선의 최대최소 각각 4개 값을 받음
 1. 4개의 값으로 BoundingBox생성하고 이를 Grid로 분할함
 2. BoundingBox내 존재하는 문화유산을 확인함
 3. BoundingBox내 존재하는 방문문화유산을 확인함

 */
@Service
@RequiredArgsConstructor
public class MapService {

  public static final double gridMinSize = 0.01;

  private final MemberRepository memberRepository;

  public List<MapGrid> createGridsWithColor(
      double north_Latitude, double south_Latitude,
      double east_Longitude, double west_Longitude, String userId
  ) {

    // bounding box 생성
    MapBoundingBox boundingBox = new MapBoundingBox(
        north_Latitude, south_Latitude, east_Longitude, west_Longitude);

    // 생성된 bounding box를 grid로 나눔
    List<MapGrid> grids = this.createGrid(boundingBox);

    // 요청한 유저의 방문문화유산을 확인
    MemberEntity memberEntity = memberRepository.findByMemberId(userId)
        .orElseThrow(() -> new CustomExcpetion(
            ErrorCode.USERID_NON_EXISTENT, "존재하지 않는 userId입니다"));

    // 방문 문화유산이 grid 내에 존재하는지 확인, 존재하면 grid 를 unblack 처리
    HashSet<HeritageEntity> visitedHeritages = memberEntity.getVisitedHeritages();
    
    for (HeritageEntity visitedHeritage : visitedHeritages) {
      double xCoordinate = visitedHeritage.getLocation().getX();
      double yCoordinate = visitedHeritage.getLocation().getY();
        unBlackGridIfExistVisitedHeritage(xCoordinate,yCoordinate,boundingBox,grids);
    }

    return grids;

  }

  /*
  client로부터 지도표시를 위해 위도의 최대최소, 경도의 최대최소, 4개 값을 받음
  grid를 위도 경도 0.01도 단위로 규격화하여 boundingbox를 생성
  boundingbox를 gridMinSize로 쪼개어 gridbox로 만들고 List에 추가함
   */

  public List<MapGrid> createGrid(MapBoundingBox boundingBox) {

    List<MapGrid> grids = new ArrayList<>();

    for (int numOfGrid_Latitude = 0;
        numOfGrid_Latitude < boundingBox.numOfGrid_Latitude;
        numOfGrid_Latitude++) {
      for (int numOfGrid_Longitude = 0;
          numOfGrid_Longitude < boundingBox.numOfGrid_Longitude;
          numOfGrid_Longitude++) {

        // boundingbox의 좌상단 끝점으로부터 grid를 1개씩 생성해하여 List에 add 진행
        double gridPoint_Longitude =
            boundingBox.west_Longitude + gridMinSize * numOfGrid_Longitude;
        double gridPoint_Latitude =
            boundingBox.north_Latitude - gridMinSize * numOfGrid_Latitude;

        grids.add(new MapGrid(gridPoint_Longitude, gridPoint_Latitude));

      }
    }

    return grids;

  }

  public void unBlackGridIfExistVisitedHeritage(double xCoordinate, double yCoordinate,
      MapBoundingBox boundingBox, List<MapGrid> grids) {

    // bounding box 외부에 있는지 확인
    if (boundingBox.west_Longitude > xCoordinate
        || xCoordinate > boundingBox.east_Longitude
        || boundingBox.south_Latitude > yCoordinate
        || yCoordinate > boundingBox.north_Latitude) {
      return;
    }
    // 내부에 있다면 몇번째 bounding box인지 확인
    int indexX = (int) ((xCoordinate - boundingBox.west_Longitude)
        / gridMinSize);
    int indexY = (int) ((boundingBox.north_Latitude - yCoordinate)
        / gridMinSize);

    int gridNum = indexY * boundingBox.numOfGrid_Longitude+indexX;

    // 몇번째 boundingbox인지 식별후 not black으로 변경

    grids.get(gridNum).setBlack(false);

  }


}
