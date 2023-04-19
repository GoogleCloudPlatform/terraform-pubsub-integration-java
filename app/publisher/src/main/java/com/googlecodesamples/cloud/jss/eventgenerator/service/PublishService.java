package com.googlecodesamples.cloud.jss.eventgenerator.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.googlecodesamples.cloud.jss.eventgenerator.converter.BaseMessageConverter;
import com.googlecodesamples.cloud.jss.eventgenerator.task.PublishTask;
import com.googlecodesamples.cloud.jss.eventgenerator.utilities.EvChargeEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
  private static final Logger log = LoggerFactory.getLogger(PublishService.class);
  private final PubSubTemplate pubSubTemplate;
  private ExecutorService executor = null;

  @Value("${event.topic}")
  private String eventTopic;

  public PublishService(PubSubTemplate pubSubTemplate, BaseMessageConverter messageConverter) {
    this.pubSubTemplate = pubSubTemplate;
    this.pubSubTemplate.setMessageConverter(messageConverter);
  }

  public void publishMsgRandom(int times, int thread, float sleep) {
    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new PublishTask(this, sleep, times));
    }
  }

  public void publishMsg(EvChargeEvent evChargeEvent) {
    log.info(
        "Thread [{}], publishing evChargeEvent to the topic [{}], message [{}]",
        Thread.currentThread().getName(),
        this.eventTopic,
        evChargeEvent);
    pubSubTemplate.publish(this.eventTopic, evChargeEvent);
  }

  public void shutdownRandom() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }
}
