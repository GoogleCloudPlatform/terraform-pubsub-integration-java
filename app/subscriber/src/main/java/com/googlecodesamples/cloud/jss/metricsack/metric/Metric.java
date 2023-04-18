package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.googlecodesamples.cloud.jss.metricsack.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetric;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

public class Metric {
  private static final Logger log = LoggerFactory.getLogger(Metric.class);

  private final PubSubTemplate pubSubTemplate;

  @Value("${metric.topic}")
  private String topic;

  protected Metric(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  public void processMessage(BasicAcknowledgeablePubsubMessage basicMessage) {
    // TODO same as Gaussian?
    float processTime = PubSubUtil.genRandomFloat(0.1f, 5);
    try {
      Thread.sleep((long) processTime * 1000);
    } catch (InterruptedException e) {
      log.error("ProcessMessage error", e);
    }
    messageAckOrNack(basicMessage);
    EvChargeMetric evChargeMetric = genEvChargeMetric(basicMessage, processTime);
    publishMessage(evChargeMetric);
  }

  public void messageAckOrNack(BasicAcknowledgeablePubsubMessage basicMessage) {
    log.info("messageAckOrNack ack");
    basicMessage.ack();
  }

  public EvChargeMetric genEvChargeMetric(
      BasicAcknowledgeablePubsubMessage basicMessage, float processTime) {
    PubsubMessage message = basicMessage.getPubsubMessage();
    EvChargeEvent evChargeEvent =
        PubSubUtil.jsonDecode(message.getData(), EvChargeEvent.getClassSchema());
    EvChargeMetric evChargeMetric = new EvChargeMetric();
    BeanUtils.copyProperties(evChargeEvent, evChargeMetric);
    evChargeMetric.setEventTimestamp(evChargeEvent.getSessionEndTime());
    // TODO weird format, used current time temporally
    evChargeMetric.setPublishTimestamp(PubSubUtil.formatTime(System.currentTimeMillis()));
    evChargeMetric.setProcessingTimeSec(processTime);
    evChargeMetric.setAckTimestamp(PubSubUtil.formatTime(System.currentTimeMillis()));
    float diffInHour =
        PubSubUtil.getDiffTimeInHour(
            evChargeEvent.getSessionEndTime().toString(),
            evChargeEvent.getSessionStartTime().toString());
    evChargeMetric.setSessionDurationHr(diffInHour);
    genExtraFields(evChargeMetric);
    return evChargeMetric;
  }

  public void genExtraFields(EvChargeMetric evChargeMetric) {}

  public void publishMessage(EvChargeMetric evChargeMetric) {
    log.info(
        "Publishing evChargeMetric to the topic [{}], message [{}]", this.topic, evChargeMetric);
    ByteString data = PubSubUtil.jsonEncode(evChargeMetric, EvChargeMetric.getClassSchema());
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
    pubSubTemplate.publish(this.topic, pubsubMessage);
  }
}
