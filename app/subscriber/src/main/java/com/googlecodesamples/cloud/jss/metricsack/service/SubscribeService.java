package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.googlecodesamples.cloud.jss.metricsack.factory.BaseSubscriberFactory;
import com.googlecodesamples.cloud.jss.metricsack.metric.Metric;import com.googlecodesamples.cloud.jss.metricsack.metric.MetricsAck;
import com.googlecodesamples.cloud.jss.metricsack.metric.MetricsComplete;
import com.googlecodesamples.cloud.jss.metricsack.metric.MetricsNack;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricAck;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricNack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {
  protected static final String METRICS_ACK = "MetricsAck";
  protected static final String METRICS_NACK = "MetricsNack";
  protected static final String METRICS_COMPLETE = "MetricsComplete";
  private final Metric<EvChargeMetricAck> metricAck;
  private final Metric<EvChargeMetricNack> metricsNack;
  private final Metric<EvChargeMetricComplete> metricsComplete;

  public SubscribeService(
      BaseSubscriberFactory subscriberFactory,
      MetricsAck metricsAck,
      MetricsNack metricsNack,
      MetricsComplete metricsComplete,
      @Value("${metric.app.type}") String metricAppType) {
    this.metricAck = metricsAck;
    this.metricsNack = metricsNack;
    this.metricsComplete = metricsComplete;
    Subscriber subscriber = subscriberFactory.createSubscriber(getMetricReceiver(metricAppType));
    subscriber.startAsync().awaitRunning();
  }

  private MessageReceiver getMetricReceiver(String metricAppType) {
    switch (metricAppType) {
      case METRICS_ACK:
        return metricAck.getReceiver();
      case METRICS_NACK:
        return metricsNack.getReceiver();
      case METRICS_COMPLETE:
        return metricsComplete.getReceiver();
      default:
        return null;
    }
  }
}
