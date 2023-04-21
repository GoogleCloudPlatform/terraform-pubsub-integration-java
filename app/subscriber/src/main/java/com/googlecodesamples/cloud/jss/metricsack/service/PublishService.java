package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
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

  public void publishMsg(PubsubMessage message) {
    log.info(
        "Publish message to the topic [{}], message [{}]", publisher.getTopicName(), message.getData().toStringUtf8());
    publisher.publish(message);
  }
}
