package com.cienet.pub.publisher;

import com.cienet.pub.util.PubUtil;
import com.cienet.pub.utilities.EvChargeEvent;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Publisher implements IPublisher {
  private static final Logger log = LoggerFactory.getLogger(Publisher.class);

  private final PubSubTemplate pubSubTemplate;

  public Publisher(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  @Value("${event.topic}")
  private String eventTopic;

  @Override
  public void publish(EvChargeEvent evChargeEvent) {
    log.info(
        "Thread [{}], publishing evChargeEvent to the topic [{}], message [{}]",
        Thread.currentThread().getName(),
        this.eventTopic,
        evChargeEvent);
    ByteString data = PubUtil.jsonEncode(evChargeEvent, EvChargeEvent.getClassSchema());
    if (data != null) {
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      pubSubTemplate.publish(this.eventTopic, pubsubMessage);
    }
  }
}
