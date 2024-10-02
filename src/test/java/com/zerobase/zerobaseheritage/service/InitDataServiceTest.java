package com.zerobase.zerobaseheritage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.zerobase.zerobaseheritage.dto.HeritageApiDto;
import com.zerobase.zerobaseheritage.repository.HeritageRepository;
import java.util.Arrays;
import java.util.Collections;
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

@ExtendWith(MockitoExtension.class)
public class InitDataServiceTest {

  @Mock
  private HeritageRepository heritageRepository;

  @InjectMocks
  private InitDataService initDataService;

  private GeometryFactory geometryFactory;

  @BeforeEach
  void setUp() {
    geometryFactory = new GeometryFactory();
  }

  @Test
  void testInitHeritageData_Success_WithNullBasicDescription() {
    // Given
    Point location = geometryFactory.createPoint(new Coordinate(125.0, 35.0));

    HeritageApiDto dto1 = HeritageApiDto.builder()
        .heritageId("H003")
        .heritageName("Heritage Three")
        .location(location)
        .heritageGrade("Grade C")
        .build();

    HeritageApiDto dto2 = HeritageApiDto.builder()
        .heritageId("H004")
        .heritageName("Heritage Four")
        .location(location)
        .heritageGrade("Grade D")
        .build();

    List<HeritageApiDto> heritageApiDtos = Arrays.asList(dto1, dto2);

    // Mocking the repository's insertIgnore method for null basicDescription
    doReturn(1).when(heritageRepository).insertIgnore(
        anyString(),
        anyString(),
        any(Point.class),
        anyString(),
        isNull() // Expecting null basicDescription
    );

    // When
    int result = initDataService.initHeritageData(heritageApiDtos);

    // Then
    assertEquals(2, result);
    verify(heritageRepository, times(2)).insertIgnore(
        anyString(),
        anyString(),
        any(Point.class),
        anyString(),
        isNull()
    );
  }


  @Test
  void testInitHeritageData_EmptyList() {
    // Given
    List<HeritageApiDto> heritageApiDtos = Collections.emptyList();

    // When
    int result = initDataService.initHeritageData(heritageApiDtos);

    // Then
    assertEquals(0, result);
    verify(heritageRepository, never()).insertIgnore(
        anyString(),
        anyString(),
        any(Point.class),
        anyString(),
        any()
    );
  }

  @Test
  void testInitHeritageData_WithDuplicatesAndNullBasicDescription() {
    // Given
    Point location = geometryFactory.createPoint(new Coordinate(125.0, 35.0));

    HeritageApiDto dto1 = HeritageApiDto.builder()
        .heritageId("H007")
        .heritageName("Heritage Seven")
        .location(location)
        .heritageGrade("Grade G")
        .build();

    HeritageApiDto dto2 = HeritageApiDto.builder()
        .heritageId("H007") // Duplicate ID
        .heritageName("Heritage Seven Duplicate")
        .location(location)
        .heritageGrade("Grade G")
        .build();

    List<HeritageApiDto> heritageApiDtos = Arrays.asList(dto1, dto2);

    // Mocking the repository's insertIgnore method
    doReturn(1).doReturn(0).when(heritageRepository).insertIgnore(
        anyString(),
        anyString(),
        any(Point.class),
        anyString(),
        isNull()
    );

    // When
    int result = initDataService.initHeritageData(heritageApiDtos);

    // Then
    assertEquals(1, result);
    verify(heritageRepository, times(2)).insertIgnore(
        anyString(),
        anyString(),
        any(Point.class),
        anyString(),
        isNull()
    );
  }
}
