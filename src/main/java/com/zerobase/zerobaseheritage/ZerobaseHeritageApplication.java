package com.zerobase.zerobaseheritage;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZerobaseHeritageApplication {


  public static void main(String[] args) {
    SpringApplication.run(ZerobaseHeritageApplication.class, args);


  }

}

