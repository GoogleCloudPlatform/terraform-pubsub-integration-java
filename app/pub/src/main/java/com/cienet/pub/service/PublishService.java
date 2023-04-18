package com.cienet.pub.service;

import com.cienet.pub.factory.BasePublisherFactory;
import com.cienet.pub.task.PublishTask;
import com.cienet.pub.util.PublishUtil;
import com.cienet.pub.utilities.EvChargeEvent;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
  private static final Logger log = LoggerFactory.getLogger(PublishService.class);
  private final Publisher publisher;
  private final BasePublisherFactory publisherFactory;
  private ExecutorService executor = null;

  public PublishService(Publisher publisher, BasePublisherFactory publisherFactory) {
    this.publisher = publisher;
    this.publisherFactory = publisherFactory;
  }

  public void publishMsg(EvChargeEvent evChargeEvent)
      throws InterruptedException, ExecutionException {
    ByteString data = PublishUtil.jsonEncode(evChargeEvent, EvChargeEvent.getClassSchema());
    ApiFuture<String> messageIdFuture = null;
    if (data != null) {
      try {
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        log.info("Publish Message: " + pubsubMessage.getData().toStringUtf8());
        messageIdFuture = publisher.publish(pubsubMessage);
      } finally {
        messageIdFuture.get();
      }
    }
  }

  public void publishMsgRandom(int times, int thread, float sleep) {
    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new PublishTask(times, sleep, this));
    }
  }

  public void shutdownRandom() throws Exception {
    if (executor == null) {
      return;
    }
    executor.shutdown();
    try {
      if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
    publisherFactory.destroyPublisher(publisher);
  }
}
