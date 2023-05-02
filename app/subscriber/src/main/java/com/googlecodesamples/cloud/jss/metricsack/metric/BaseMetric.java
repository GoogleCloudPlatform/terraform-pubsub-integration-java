package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsComplete;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import java.io.IOException;
import java.time.Instant;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public abstract class BaseMetric<T> {
  private static final Logger log = LoggerFactory.getLogger(BaseMetric.class);

  protected final PublishService publishService;

  private final MessageService messageService;

  protected BaseMetric(PublishService publishService, MessageService messageService) {
    this.publishService = publishService;
    this.messageService = messageService;
  }

  public abstract T genMetricMessage(Event event, float processTime, Timestamp publishTime);

  public abstract Schema getSchema();

  public MessageReceiver getReceiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      try {
        log.info("Receive message [{}]", message.getData().toStringUtf8());
        T newMessage = processMessage(message, consumer);
        publishService.publishMsg(messageService.toPubSubMessage(newMessage, getSchema()));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public final T processMessage(PubsubMessage message, AckReplyConsumer consumer)
      throws InterruptedException, IOException {
    float processTime = genProcessTime();
    Thread.sleep((long) (processTime * 1000));
    consumerAckOrNack(consumer);
    Event event = messageService.fromPubSubMessage(message);
    return genMetricMessage(event, processTime, message.getPublishTime());
  }

  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("ConsumerAckOrNack ack");
    consumer.ack();
  }

  public final MetricsComplete genCommonMetricMessage(
      Event event, float processTime, Timestamp publishTime) {
    MetricsComplete metricMessage = new MetricsComplete();
    BeanUtils.copyProperties(event, metricMessage);
    metricMessage.setEventTimestamp(event.getSessionEndTime());
    metricMessage.setPublishTimestamp(Instant.ofEpochSecond(publishTime.getSeconds()));
    metricMessage.setProcessingTimeSec(PubSubUtil.formatFloat(processTime));
    metricMessage.setAckTimestamp(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
    float diffInHour =
        PubSubUtil.getDiffTimeInHour(event.getSessionEndTime(), event.getSessionStartTime());
    metricMessage.setSessionDurationHr(diffInHour);
    return metricMessage;
  }

  private float genProcessTime() {
    float ratio = PubSubUtil.genRandomFloat(0, 100);
    if (ratio <= 0.1) {
      return PubSubUtil.genRandomFloat(0.1f, 5);
    } else {
      return PubSubUtil.genRandomFloat(0.1f, 0.3f);
    }
  }
}
