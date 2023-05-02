package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsAck;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsNack;
import com.googlecodesamples.cloud.jss.metricsack.factory.BaseSubscriberFactory;
import com.googlecodesamples.cloud.jss.metricsack.metric.AckMetric;
import com.googlecodesamples.cloud.jss.metricsack.metric.BaseMetric;
import com.googlecodesamples.cloud.jss.metricsack.metric.CompleteMetric;
import com.googlecodesamples.cloud.jss.metricsack.metric.NackMetric;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {
  private static final Logger log = LoggerFactory.getLogger(SubscribeService.class);

  protected static final String METRICS_ACK = "MetricsAck";
  protected static final String METRICS_NACK = "MetricsNack";
  protected static final String METRICS_COMPLETE = "MetricsComplete";
  private final BaseMetric<MetricsAck> ackMetric;
  private final BaseMetric<MetricsNack> nackMetric;
  private final BaseMetric<MetricsComplete> completeMetric;
  private final Subscriber subscriber;

  public SubscribeService(
      BaseSubscriberFactory subscriberFactory,
      BaseMetric<MetricsAck> ackMetric,
      BaseMetric<MetricsNack> nackMetric,
      BaseMetric<MetricsComplete> completeMetric,
      @Value("${metric.app.type}") String metricAppType)
      throws IllegalArgumentException {
    this.ackMetric = ackMetric;
    this.nackMetric = nackMetric;
    this.completeMetric = completeMetric;
    subscriber = subscriberFactory.createSubscriber(getMetricReceiver(metricAppType));
    subscriber.startAsync().awaitRunning();
  }

  private MessageReceiver getMetricReceiver(String metricAppType) throws IllegalArgumentException {
    log.info("Metric app type [{}] start", metricAppType);
    return switch (metricAppType) {
      case METRICS_ACK -> ackMetric.getReceiver();
      case METRICS_NACK -> nackMetric.getReceiver();
      case METRICS_COMPLETE -> completeMetric.getReceiver();
      default -> throw new IllegalArgumentException("Metric app type should be specified");
    };
  }

  @PreDestroy
  private void cleanUp() {
    subscriber.stopAsync();
  }
}
