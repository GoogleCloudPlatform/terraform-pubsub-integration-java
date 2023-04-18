package com.cienet.sub.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.stereotype.Component;

@Component
public class MetricsNack extends Metric {
  private static final Logger log = LoggerFactory.getLogger(MetricsNack.class);

  protected MetricsNack(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }

  @Override
  public void messageAckOrNack(BasicAcknowledgeablePubsubMessage basicMessage) {
    log.info("messageAckOrNack nack");
    basicMessage.nack(); // nack every message receives 🐞
  }
}
