package com.cienet.sub.metric;

import com.cienet.sub.service.PublishService;
import com.cienet.sub.util.SubscribeUtil;
import com.cienet.sub.utilities.EvChargeEvent;
import com.cienet.sub.utilities.EvChargeMetricComplete;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public abstract class Metric<T> {
  private static final Logger log = LoggerFactory.getLogger(Metric.class);

  protected final PublishService publishService;

  protected Metric(PublishService publishService) {
    this.publishService = publishService;
  }

  public abstract ByteString convertMessage(T message);

  public abstract T genMetricData(EvChargeEvent evChargeEvent, float processTime);

  public MessageReceiver receiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      try {
        log.info("Receive Message: " + message.getData().toStringUtf8());
        T newMessage = processMessage(message, consumer);
        publishService.publishMsg(convertMessage(newMessage));
      } catch (Exception e) {
        log.error("Receive Exception", e);
      }
    };
  }

  public final T processMessage(PubsubMessage message, AckReplyConsumer consumer) {
    float processTime = SubscribeUtil.genRandomFloat(0.1f, 5);
    try {
      Thread.sleep((long) processTime * 1000);
    } catch (InterruptedException e) {
      log.error("ProcessMessage error", e);
    }
    consumerAckOrNack(consumer);
    EvChargeEvent evChargeEvent =
        SubscribeUtil.jsonDecode(message.getData(), EvChargeEvent.getClassSchema());
    return genMetricData(evChargeEvent, processTime);
  }

  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("consumerAckOrNack ack");
    consumer.ack();
  }

  public final EvChargeMetricComplete genCommonData(
      EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricComplete evChargeMetric = new EvChargeMetricComplete();
    BeanUtils.copyProperties(evChargeEvent, evChargeMetric);
    evChargeMetric.setEventTimestamp(evChargeEvent.getSessionEndTime());
    // TODO
    evChargeMetric.setPublishTimestamp(SubscribeUtil.formatTime(System.currentTimeMillis()));
    evChargeMetric.setProcessingTimeSec(processTime);
    evChargeMetric.setAckTimestamp(SubscribeUtil.formatTime(System.currentTimeMillis()));
    float diffInHour =
        SubscribeUtil.getDiffTimeInHour(
            evChargeEvent.getSessionEndTime().toString(),
            evChargeEvent.getSessionStartTime().toString());
    evChargeMetric.setSessionDurationHr(diffInHour);
    return evChargeMetric;
  }
}
