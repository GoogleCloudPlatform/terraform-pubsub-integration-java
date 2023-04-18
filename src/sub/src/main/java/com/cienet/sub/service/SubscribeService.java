package com.cienet.sub.service;

import com.cienet.sub.factory.BaseSubscriberFactory;
import com.cienet.sub.metric.MetricsAck;
import com.cienet.sub.metric.MetricsComplete;
import com.cienet.sub.metric.MetricsNack;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {

  private final MetricsAck metricAck;
  private final MetricsNack metricsNack;
  private final MetricsComplete metricsComplete;

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
      case "MetricsNack":
        return metricsNack.receiver();
      case "MetricsComplete":
        return metricsComplete.receiver();
      default:
        return metricAck.receiver();
    }
  }
}
