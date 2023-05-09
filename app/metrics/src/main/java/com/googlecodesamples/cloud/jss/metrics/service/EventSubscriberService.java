/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecodesamples.cloud.jss.metrics.service;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.googlecodesamples.cloud.jss.common.generated.MetricsAck;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.generated.MetricsNack;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.metrics.factory.EventSubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class EventSubscriberService {
  private static final Logger logger = LoggerFactory.getLogger(EventSubscriberService.class);
  protected static final String METRICS_ACK = "MetricsAck";
  protected static final String METRICS_NACK = "MetricsNack";
  protected static final String METRICS_COMPLETE = "MetricsComplete";
  private final BaseAction<MetricsAck> ackMetric;
  private final BaseAction<MetricsNack> nackMetric;
  private final BaseAction<MetricsComplete> completeMetric;
  private final Subscriber subscriber;

  public EventSubscriberService(
          EventSubscriberFactory factory,
          BaseAction<MetricsAck> ackMetric,
          BaseAction<MetricsNack> nackMetric,
          BaseAction<MetricsComplete> completeMetric,
          @Value("${metric.app.type}") String metricAppType)
          throws IllegalArgumentException {
    this.ackMetric = ackMetric;
    this.nackMetric = nackMetric;
    this.completeMetric = completeMetric;
    subscriber = factory.createSubscriber(getMetricReceiver(metricAppType));
    subscriber.startAsync().awaitRunning();
  }

  /**
   * Get a receiver, which defines actions when receiving a message.
   *
   * @param metricAppType type of the metric
   * @return the message receiver
   */
  private MessageReceiver getMetricReceiver(String metricAppType) throws IllegalArgumentException {
    logger.info("metric app type: {}", metricAppType);
    return switch (metricAppType) {
      case METRICS_ACK -> ackMetric.getReceiver();
      case METRICS_NACK -> nackMetric.getReceiver();
      case METRICS_COMPLETE -> completeMetric.getReceiver();
      default -> throw new IllegalArgumentException("Metric app type should be specified");
    };
  }

  /**
   * Stop pulling messages and release resources.
   */
  @PreDestroy
  public void cleanUp() {
    subscriber.stopAsync();
  }
}
