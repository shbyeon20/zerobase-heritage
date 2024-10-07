package com.zerobase.zerobaseheritage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import com.zerobase.zerobaseheritage.datatype.exception.CustomExcpetion;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import com.zerobase.zerobaseheritage.dto.HeritageDto;
import com.zerobase.zerobaseheritage.entity.HeritageEntity;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import com.zerobase.zerobaseheritage.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class ZerobaseHeritageApplicationTests {

  @Mock
  private HeritageRepository heritageRepository;

  @InjectMocks
  private SearchService searchService;

  private GeometryFactory geometryFactory;

  @BeforeEach
  void setUp() {
    geometryFactory = new GeometryFactory();
  }


  @Test
  void testByPointLocationSuccess() {
    //given

    Point point = geometryFactory.createPoint(new Coordinate(125, 35));

    given(heritageRepository.findWithinDistance(any(Point.class), anyInt()))
        .willReturn(List.of(HeritageEntity.builder()
            .id(2L)
            .heritageId("heritage1")
            .heritageName("Heritage Name 1")
            .heritageGrade("국보")
            .location(point)  // Mocked point object
            .build()));

    //when
    List<HeritageDto> heritageDtos = searchService.byPointLocation(point);

    //then
    assertNotNull(heritageDtos);  // Check if result is not null
    assertEquals(1, heritageDtos.size());  // Verify size of the result
    assertEquals("heritage1", heritageDtos.get(0).getHeritageId());  // Check specific properties
    assertEquals("Heritage Name 1", heritageDtos.get(0).getHeritageName());
  }

  /*
  좌표가 국내 위치가 아니라면 exception 발생
   */
  @Test
  void testByPointLocationFail() {
    //given

    Point point = geometryFactory.createPoint(new Coordinate(1, 1));

    //when
    CustomExcpetion customExcpetion = assertThrows(CustomExcpetion.class,
        () -> searchService.byPointLocation(point));

    //then

    assertEquals(ErrorCode.LOCATION_OUT_OF_BOUND,customExcpetion.getErrorCode());

  }

}
