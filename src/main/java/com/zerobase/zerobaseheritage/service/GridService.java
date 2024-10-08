package com.zerobase.zerobaseheritage.service;

import com.zerobase.zerobaseheritage.datatype.MapBoundingBox;
import com.zerobase.zerobaseheritage.datatype.MapGrid;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GridService {

  // 위도 경도 0.01도를 기준으로 GRID 분할
  public static final double GRID_MIN_SIZE = 0.01;

/*
  client로부터 지도표시를 위해 위도의 최대최소, 경도의 최대최소, 4개 값을 받음
  grid를 위도 경도 0.01도 단위로 규격화하여 boundingbox를 생성
  boundingbox를 gridMinSize로 쪼개어 gridbox로 만들고 List에 추가함
   */

  public List<MapGrid> createGridsFromBoundingBox(MapBoundingBox boundingBox) {
    List<MapGrid> grids = new ArrayList<>();
    // bounding box로 생성할수있는 grid의 가로 세로 개수 계산
    int totalNumOfGridsByLongitudeInBox = calculateNumOfGridsByLongitudeInBox(
        boundingBox);

    log.info(totalNumOfGridsByLongitudeInBox
        + "  piece of grids created By Longitude");

    int totalNumOfGridsByLatitudeInBox = calculateNumOfGridsByLatitudeInBox(
        boundingBox);

    log.info(
        totalNumOfGridsByLatitudeInBox + " piece of grids created By Latitude");

    // grid를 좌상단부터 우측으로 하나씩 순회
    for (int indexOfGridByLatitude = 0;
        indexOfGridByLatitude < totalNumOfGridsByLatitudeInBox;
        indexOfGridByLatitude++) {
      for (int indexOfGridByLongitude = 0;
          indexOfGridByLongitude < totalNumOfGridsByLongitudeInBox;
          indexOfGridByLongitude++) {

        // 각 grid의 좌상단 point를 기준으로 grid 객체 생성하여 리스트에 저장, double 의 floating number 끝자리 오류로 round 처리
        double pointLongitudeOfGrid =
            Math.round((boundingBox.westLongitude
                + GRID_MIN_SIZE * indexOfGridByLongitude) * 100) / 100.0;
        double pointLatitudeOfGrid =
            Math.round((boundingBox.northLatitude
                - GRID_MIN_SIZE * indexOfGridByLatitude) * 100) / 100.0;
        log.info("grid : " + pointLatitudeOfGrid + "/" + pointLongitudeOfGrid);

        grids.add(new MapGrid(pointLongitudeOfGrid,
            pointLatitudeOfGrid, GRID_MIN_SIZE));

      }
    }

    return grids;

  }

  // bounding box 내에 가로 grid 개수 산출

  private int calculateNumOfGridsByLongitudeInBox(MapBoundingBox boundingBox) {
    return (int) Math.round(
        ((boundingBox.northLatitude - boundingBox.southLatitude)
            / GRID_MIN_SIZE));
  }

  // bounding box 내에 grid 세로 개수 산출
  private int calculateNumOfGridsByLatitudeInBox(MapBoundingBox boundingBox) {
    return (int) Math.round(
        ((boundingBox.eastLongitude - boundingBox.westLongitude)
            / GRID_MIN_SIZE));
  }

  public void unBlackGridIfPointExistInGrid(double pointLongitude,
      double pointLatitude,
      MapBoundingBox boundingBox, List<MapGrid> grids) {

    // bounding box 외부에 있는지 확인
    if (boundingBox.westLongitude > pointLongitude
        || pointLongitude > boundingBox.eastLongitude
        || boundingBox.southLatitude > pointLatitude
        || pointLatitude > boundingBox.northLatitude) {
      return;
    }
    // 내부에 있다면 몇번째 grid 인지 확인
    // point가 위치하는 grid의 index를 확인
    int indexOfGridByLongitude = (int) (
        (pointLongitude - boundingBox.westLongitude)
            / GRID_MIN_SIZE);
    int indexOfGridByLatitude = (int) (
        (boundingBox.northLatitude - pointLatitude)
            / GRID_MIN_SIZE);
    int numOfGridsByLongitudeInBox = calculateNumOfGridsByLongitudeInBox(
        boundingBox);

    // index를 사용하여 몇번째 grid인지 확인.
    int gridNum =
        indexOfGridByLongitude * numOfGridsByLongitudeInBox
            + indexOfGridByLatitude;

    // 몇번째 boundingbox인지 식별후 not black으로 변경

    grids.get(gridNum).setBlack(false);

  }
}
