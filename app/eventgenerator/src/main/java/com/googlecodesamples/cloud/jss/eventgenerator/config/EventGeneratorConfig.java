package com.googlecodesamples.cloud.jss.eventgenerator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configurations for the event generator, which generates randomized event messages. */
@Configuration
@ConfigurationProperties(prefix = "event.generator")
public class EventGeneratorConfig {

  private static final String ERROR_MSG_NEGATIVE_THREADS =
      "The threads for event generator should be greater than zero";

  private static final String ERROR_MSG_NEGATIVE_RUNTIME =
      "The runtime for event generator should be greater than zero";

  private Integer threads;

  private Float runtime;

  public Integer getThreads() {
    return threads;
  }

  public void setThreads(Integer threads) throws IllegalArgumentException {
    if (threads <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_THREADS);
    }
    this.threads = threads;
  }

  public Float getRuntime() {
    return runtime;
  }

  public void setRuntime(Float runtime) throws IllegalArgumentException {
    if (runtime <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_RUNTIME);
    }
    this.runtime = runtime;
  }
}
