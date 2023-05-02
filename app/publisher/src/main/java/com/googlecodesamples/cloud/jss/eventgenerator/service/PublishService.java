package com.googlecodesamples.cloud.jss.eventgenerator.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import com.googlecodesamples.cloud.jss.eventgenerator.task.PublishTask;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
  private static final Logger log = LoggerFactory.getLogger(PublishService.class);
  private final Publisher publisher;
  private final MessageService messageService;
  private ExecutorService executor = null;

  public PublishService(Publisher publisher, MessageService messageService) {
    this.publisher = publisher;
    this.messageService = messageService;
  }

  public void publishMsg(Event event) throws InterruptedException, ExecutionException, IOException {
    log.info(
        "Thread [{}], publish event to the topic [{}], message [{}]",
        Thread.currentThread().getName(),
        publisher.getTopicName(),
        event);
    PubsubMessage message = messageService.toPubSubMessage(event);
    ApiFuture<String> messageId = publisher.publish(message);
    log.info("Message id [{}] received, message [{}]", messageId.get(), event);
  }

  public void publishMsgRandom(int times, int thread, float sleep) {
    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new PublishTask(this, sleep, times));
    }
  }

  public void shutdownRandom() {
    if (executor == null) {
      return;
    }
    try {
      executor.shutdown();
      if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }

  @PreDestroy
  private void cleanUp() {
    shutdownRandom();
  }
}
