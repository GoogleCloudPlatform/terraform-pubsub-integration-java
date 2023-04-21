package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import com.googlecodesamples.cloud.jss.metricsack.util.SubscribeUtil;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public abstract class Metric<T> {
  private static final Logger log = LoggerFactory.getLogger(Metric.class);

  protected final PublishService publishService;

  private final MessageService messageService;

  protected Metric(PublishService publishService, MessageService messageService) {
    this.publishService = publishService;
    this.messageService = messageService;
  }

  public abstract T genMetricMessage(
      EvChargeEvent evChargeEvent, float processTime, Timestamp publishTime);

  public MessageReceiver getReceiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      log.info("Receive message [{}]", message.getData().toStringUtf8());
      T newMessage = processMessage(message, consumer);
      publishService.publishMsg(messageService.toPubSubMessage(newMessage));
    };
  }

  public final T processMessage(PubsubMessage message, AckReplyConsumer consumer) {
    float processTime = SubscribeUtil.genRandomFloat(0.1f, 5);
    try {
      Thread.sleep((long) (processTime * 1000));
    } catch (InterruptedException e) {
      log.error("ProcessMessage InterruptedException", e);
    }
    consumerAckOrNack(consumer);
    EvChargeEvent evChargeEvent = messageService.fromPubSubMessage(message);
    return genMetricMessage(evChargeEvent, processTime, message.getPublishTime());
  }

  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("ConsumerAckOrNack ack");
    consumer.ack();
  }

  public final EvChargeMetricComplete genCommonMetricMessage(
      EvChargeEvent evChargeEvent, float processTime, Timestamp publishTime) {
    EvChargeMetricComplete metricMessage = new EvChargeMetricComplete();
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
