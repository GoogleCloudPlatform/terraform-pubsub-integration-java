package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.metricsack.util.SubscribeUtil;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

public abstract class Metric<T> {
  private static final Logger log = LoggerFactory.getLogger(Metric.class);
  private final PubSubTemplate pubSubTemplate;

  @Value("${metric.topic}")
  private String metricTopic;

  protected Metric(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  public abstract T genMetricMessage(
      ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message, float processTime);

  public final void processMessage(
      ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message) {
    float processTime = SubscribeUtil.genRandomFloat(0.1f, 5);
    try {
      Thread.sleep((long) (processTime * 1000));
    } catch (InterruptedException e) {
      log.error("ProcessMessage InterruptedException", e);
    }
    messageAckOrNack(message);
    publishMessage(genMetricMessage(message, processTime));
  }

  public void messageAckOrNack(ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message) {
    log.info("MessageAckOrNack ack");
    message.ack();
  }

  public final void publishMessage(T evChargeMetric) {
    log.info(
        "Publishing evChargeMetric to the topic [{}], message [{}]",
        this.metricTopic,
        evChargeMetric);
    pubSubTemplate.publish(this.metricTopic, evChargeMetric);
  }

  public final EvChargeMetricComplete genCommonMetricMessage(
      ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message, float processTime) {
    EvChargeMetricComplete metricMessage = new EvChargeMetricComplete();
    EvChargeEvent evChargeEvent = message.getPayload();
    Timestamp publishTime = message.getPubsubMessage().getPublishTime();
    BeanUtils.copyProperties(evChargeEvent, metricMessage);
    metricMessage.setEventTimestamp(evChargeEvent.getSessionEndTime());
    metricMessage.setPublishTimestamp(SubscribeUtil.formatTime(publishTime.getSeconds()));
    metricMessage.setProcessingTimeSec(SubscribeUtil.formatFloat(processTime));
    metricMessage.setAckTimestamp(SubscribeUtil.formatTime(Instant.now().getEpochSecond()));
    float diffInHour =
        SubscribeUtil.getDiffTimeInHour(
            evChargeEvent.getSessionEndTime().toString(),
            evChargeEvent.getSessionStartTime().toString());
    metricMessage.setSessionDurationHr(diffInHour);
    return metricMessage;
  }
}
