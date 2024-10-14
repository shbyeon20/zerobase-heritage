package com.zerobase.zerobaseheritage.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class SyncConfig {

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    int cores = Runtime.getRuntime().availableProcessors();
    log.info("Available core numbers = {}", cores);

    executor.setCorePoolSize(cores * 4);  // IO bound를 상정한 배수설정
    executor.setMaxPoolSize(cores * 8);
    executor.setQueueCapacity(200);
    executor.initialize();
    return executor;

  }

}
