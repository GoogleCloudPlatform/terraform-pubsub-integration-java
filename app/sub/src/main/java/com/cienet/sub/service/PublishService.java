package com.cienet.sub.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
  private static final Logger log = LoggerFactory.getLogger(PublishService.class);

  private final Publisher publisher;

  public PublishService(Publisher publisher) {
    this.publisher = publisher;
  }

  public void publishMsg(ByteString data) throws InterruptedException, ExecutionException {
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
}
