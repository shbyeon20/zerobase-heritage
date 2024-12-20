package com.zerobase.zerobaseheritage.datatype;

import com.zerobase.zerobaseheritage.datatype.exception.CustomException;
import com.zerobase.zerobaseheritage.datatype.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum HeritageGrade {
  NATIONAL_TREASURE("국보", 5),
  TREASURE("보물", 4),
  HISTORIC_SITE("사적", 2),
  HISTORIC_AND_SCENIC_SITE("사적및명승", 2),
  SCENIC_SITE("명승", 2),
  NATURAL_MONUMENT("천연기념물", 2),
  INTANGIBLE_CULTURAL_HERITAGE("국가무형문화유산", 1),
  FOLK_CULTURAL_HERITAGE("국가민속문화유산", 1),
  CITY_TANGIBLE_CULTURAL_HERITAGE("시도유형문화유산", 1),
  CITY_INTANGIBLE_CULTURAL_HERITAGE("시도무형문화유산", 1),
  CITY_INTANGIBLE_HERITAGE("시도무형유산", 1),
  CITY_MONUMENT("시도기념물", 1),
  CITY_FOLK_CULTURAL_HERITAGE("시도민속문화유산", 1),
  CITY_REGISTERED_CULTURAL_HERITAGE("시도등록문화유산", 1),
  CULTURAL_HERITAGE_MATERIAL("문화유산자료", 1),
  NATIONAL_REGISTERED_CULTURAL_HERITAGE("국가등록문화유산", 1),
  NORTH_KOREAN_INTANGIBLE_CULTURAL_HERITAGE("이북5도무형문화유산", 0);

  // Field to store the Korean name and grade point
  private final String koreanName;
  private final int gradePoint;

  // Constructor
  HeritageGrade(String koreanName, int gradePoint) {
    this.koreanName = koreanName;
    this.gradePoint = gradePoint;
  }

  // Static method to convert Korean string to the corresponding grade point
  public static int toGradePoint(String koreanName) {
    for (HeritageGrade grade : HeritageGrade.values()) {
      if (grade.getKoreanName().equalsIgnoreCase(koreanName)) {
        return grade.getGradePoint();
      }
    }
    throw new CustomException(ErrorCode.HERITAGE_GRADE_NOT_FOUND,
        "Unknown heritage grade: " + koreanName);
  }
}
