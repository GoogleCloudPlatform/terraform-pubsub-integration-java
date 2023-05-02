package com.googlecodesamples.cloud.jss.metricsack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "subscriber")
public class SubscribeConfig {
  private String eventSubscription;
  private int parallelPull;
  private long outstandingMessages;
  private int executorThreads;

  public String getEventSubscription() {
    return eventSubscription;
  }

  public void setEventSubscription(String eventSubscription) throws IllegalArgumentException {
    if (!StringUtils.hasText(eventSubscription)) {
      throw new IllegalArgumentException("EventSubscription should not be null");
    }
    this.eventSubscription = eventSubscription;
  }

  public int getParallelPull() {
    return parallelPull;
  }

  public void setParallelPull(int parallelPull) {
    this.parallelPull = parallelPull;
  }

  public long getOutstandingMessages() {
    return outstandingMessages;
  }

  public void setOutstandingMessages(long outstandingMessages) {
    this.outstandingMessages = outstandingMessages;
  }

  public int getExecutorThreads() {
    return executorThreads;
  }

  public void setExecutorThreads(int executorThreads) throws IllegalArgumentException {
    if (executorThreads <= 0) {
      throw new IllegalArgumentException("Subscriber executor thread should greater than zero");
    }
    this.executorThreads = executorThreads;
  }

  public String toString() {
    return String.format(
        "EventSubscription %s, flow Control outStanding message %d, parallel pull %d, executor thread %d",
        eventSubscription, outstandingMessages, parallelPull, executorThreads);
  }
}
