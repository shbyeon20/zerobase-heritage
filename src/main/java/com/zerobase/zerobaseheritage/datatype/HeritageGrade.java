package com.zerobase.zerobaseheritage.datatype;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.Getter;
import lombok.Setter;

/*
API 상의 문화유산등급을 점수로 맵핑하기 위한 Enum
 */

public enum HeritageGrade {
  국보(5), 보물(4), 사적(2), 사적및명승(2), 명승(2),
  천연기념물(2), 국가무형문화유산(1), 국가민속문화유산(1),
  시도유형문화유산(1), 시도무형문화유산(1),
  시도기념물(1), 시도민속문화유산(1),
  시도등록문화유산(1), 문화유산자료(1),
  국가등록문화유산(1), 이북5도무형문화유산(0);


  private Integer gradePoint;


  HeritageGrade(Integer l) {
    this.gradePoint = l;
  }
  }
