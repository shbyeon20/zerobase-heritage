package com.zerobase.zerobaseheritage.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class SyncConfig {

  @Bean
  public ThreadPoolTaskExecutor ExternalApiTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    int cores = Runtime.getRuntime().availableProcessors();
    log.info("Available core numbers = {}", cores);

    executor.setCorePoolSize(cores * 2);  // IO bound 를 상정한 배수설정
    executor.setMaxPoolSize(cores * 2);
    executor.setQueueCapacity(1000);
    executor.initialize();
    return executor;

  }

}
