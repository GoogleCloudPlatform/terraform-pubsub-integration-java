package com.googlecodesamples.cloud.jss.metricsack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "publisher")
public class PublishConfig {
  private String metricsTopic;
  private int executorThreads;
  private long batchSize;

  public String getMetricsTopic() {
    return metricsTopic;
  }

  public void setMetricsTopic(String metricsTopic) throws Exception {
    if (!StringUtils.hasText(metricsTopic)) {
      throw new Exception("MetricsTopic should not be null");
    }
    this.metricsTopic = metricsTopic;
  }

  public int getExecutorThreads() {
    return executorThreads;
  }

  public void setExecutorThreads(int executorThreads) throws Exception {
    if (executorThreads <= 0) {
      throw new Exception("Publisher executor thread should greater than zero");
    }
    this.executorThreads = executorThreads;
  }

  public long getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(long batchSize) throws Exception {
    if (batchSize <= 0) {
      throw new Exception("Publisher batch size should greater than zero");
    }
    this.batchSize = batchSize;
  }

  public String toString() {
    return String.format(
        "MetricTopic %s, batch message %d, executor thread %d",
        metricsTopic, batchSize, executorThreads);
  }
}
