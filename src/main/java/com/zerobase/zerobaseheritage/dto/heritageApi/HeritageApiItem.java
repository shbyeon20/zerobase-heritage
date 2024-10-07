package com.zerobase.zerobaseheritage.dto.heritageApi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.zerobase.zerobaseheritage.dto.HeritageApiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeritageApiItem {

  @JacksonXmlProperty(localName = "ccbaCpno")
  private String heritageId; //
  @JacksonXmlProperty(localName = "ccbaMnm1")
  private String heritageName;
  private Double longitude;
  private Double latitude;
  @JacksonXmlProperty(localName = "ccmaName")
  private String heritageGrade;

  //ccbaKdcd, ccbaAsno, ccbaCtcd는 다른 APi 호출을 위해 필요한 para값
  private String ccbaKdcd;
  private String ccbaAsno;
  private String ccbaCtcd;





}
