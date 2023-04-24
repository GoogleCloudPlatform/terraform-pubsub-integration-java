package com.googlecodesamples.cloud.jss.eventgenerator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "publisher")
public class PublishConfig {
  private String eventTopic;
  private long outstandingMessages;
  private int executorThreads;

  public String getEventTopic() {
    return eventTopic;
  }

  public void setEventTopic(String eventTopic) throws Exception {
    if (!StringUtils.hasText(eventTopic)) {
      throw new Exception("EventTopic should not be null");
    }
    this.eventTopic = eventTopic;
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

  public void setExecutorThreads(int executorThreads) throws Exception {
    if (executorThreads <= 0) {
      throw new Exception("Publisher executor thread should greater than zero");
    }
    this.executorThreads = executorThreads;
  }

  public String toString() {
    return String.format(
        "EventTopic %s, flow Control outStanding message %d, executor thread %d",
        eventTopic, outstandingMessages, executorThreads);
  }
}
