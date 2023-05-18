package com.googlecodesamples.cloud.jss.eventgenerator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configurations for the event generator, which generates randomized event messages. */
@Configuration
@ConfigurationProperties(prefix = "event.generator")
public class EventGeneratorConfig {

  private Integer threads;

  private Float runtime;

  public Integer getThreads() {
    return threads;
  }

  public void setThreads(Integer threads) {
    this.threads = threads;
  }

  public Float getRuntime() {
    return runtime;
  }

  public void setRuntime(Float runtime) {
    this.runtime = runtime;
  }
}
