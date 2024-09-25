package com.zerobase.zerobaseheritage.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class Config {

  @Bean
  public static XmlMapper xmlMapper() {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return xmlMapper;
  }


}
