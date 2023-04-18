package com.cienet.sub.subscriber;

import com.cienet.sub.metric.Metric;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Subscriber implements ISubscriber {
  private static final Logger log = LoggerFactory.getLogger(Subscriber.class);

  private final PubSubTemplate pubSubTemplate;
  private final Metric metricsAck;
  private final Metric metricsNack;
  private final Metric metricsComplete;

  @Value("${event.subscription}")
  private String eventSubscription;

  @Value("${metric.app.type}")
  private String appType;

  public Subscriber(
      PubSubTemplate pubSubTemplate,
      Metric metricsAck,
      Metric metricsNack,
      Metric metricsComplete) {
    this.pubSubTemplate = pubSubTemplate;
    this.metricsAck = metricsAck;
    this.metricsNack = metricsNack;
    this.metricsComplete = metricsComplete;
  }

  @Override
  public void pull(BasicAcknowledgeablePubsubMessage basicMessage) {
    PubsubMessage message = basicMessage.getPubsubMessage();
    log.info("Pulling message [{}]", message.getData().toStringUtf8());
    processMessage(basicMessage);
  }

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public void subscribe() {
    log.info("Listen to [{}]", eventSubscription);
    pubSubTemplate.subscribe(this.eventSubscription, this::pull);
  }

  private void processMessage(BasicAcknowledgeablePubsubMessage basicMessage) {
    switch (appType) {
      case "MetricsAck":
        log.info("SubType MetricsAck");
        metricsAck.processMessage(basicMessage);
        break;
      case "MetricsNack":
        log.info("SubType MetricsNack");
        metricsNack.processMessage(basicMessage);
        break;
      case "MetricsComplete":
        log.info("SubType MetricsComplete");
        metricsComplete.processMessage(basicMessage);
        break;
      default:
        break;
    }
  }
}
