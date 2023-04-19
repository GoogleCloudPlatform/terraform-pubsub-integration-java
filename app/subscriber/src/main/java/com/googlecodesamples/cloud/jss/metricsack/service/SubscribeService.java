package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.googlecodesamples.cloud.jss.metricsack.converter.BaseMessageConverter;
import com.googlecodesamples.cloud.jss.metricsack.metric.Metric;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricAck;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricNack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {
  private static final Logger log = LoggerFactory.getLogger(SubscribeService.class);
  public static final String METRICS_ACK = "MetricsAck";
  public static final String METRICS_NACK = "MetricsNack";
  public static final String METRICS_COMPLETE = "MetricsComplete";
  private final PubSubTemplate pubSubTemplate;
  private final Metric<EvChargeMetricAck> metricsAck;
  private final Metric<EvChargeMetricNack> metricsNack;
  private final Metric<EvChargeMetricComplete> metricsComplete;

  @Value("${event.subscription}")
  private String eventSubscription;

  @Value("${metric.app.type}")
  private String metricAppType;

  public SubscribeService(
      PubSubTemplate pubSubTemplate,
      BaseMessageConverter messageConverter,
      Metric<EvChargeMetricAck> metricsAck,
      Metric<EvChargeMetricNack> metricsNack,
      Metric<EvChargeMetricComplete> metricsComplete) {
    this.pubSubTemplate = pubSubTemplate;
    this.pubSubTemplate.setMessageConverter(messageConverter);
    this.metricsAck = metricsAck;
    this.metricsNack = metricsNack;
    this.metricsComplete = metricsComplete;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void subscribe() {
    log.info("Subscriber listen to [{}]", eventSubscription);
    pubSubTemplate.subscribeAndConvert(this.eventSubscription, this::receiver, EvChargeEvent.class);
  }

  public void receiver(ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message) {
    log.info("Receive message [{}]", message.getPubsubMessage().getData().toStringUtf8());
    processMessage(message);
  }

  private void processMessage(ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message) {
    switch (metricAppType) {
      case METRICS_ACK:
        metricsAck.processMessage(message);
        break;
      case METRICS_NACK:
        metricsNack.processMessage(message);
        break;
      case METRICS_COMPLETE:
        metricsComplete.processMessage(message);
        break;
      default:
        break;
    }
  }
}
