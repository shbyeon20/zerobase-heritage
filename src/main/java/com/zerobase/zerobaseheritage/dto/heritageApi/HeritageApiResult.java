package com.zerobase.zerobaseheritage.dto.heritageApi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "result")
public class HeritageApiResult {

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "item")
  List<HeritageApiItem> heritageApiItemList;
  int totalCnt;


  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HeritageApiItem {

    @JacksonXmlProperty(localName = "ccbaCpno")
    private String heritageId; //
    @JacksonXmlProperty(localName = "ccbaMnm1")
    private String heritageName;
    private double longitude;
    private double latitude;
    @JacksonXmlProperty(localName = "ccmaName")
    private String heritageGrade;

    //ccbaKdcd, ccbaAsno, ccbaCtcd는 다른 APi 호출을 위해 필요한 para값
    private String ccbaKdcd;
    private String ccbaAsno;
    private String ccbaCtcd;





  }
}
