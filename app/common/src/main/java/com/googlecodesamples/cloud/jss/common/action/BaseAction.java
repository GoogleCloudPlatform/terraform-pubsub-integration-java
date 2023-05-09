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

package com.googlecodesamples.cloud.jss.common.action;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import com.googlecodesamples.cloud.jss.common.util.MessageUtil;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

public abstract class BaseAction<T> {

  private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

  protected final BasePublisherService service;

  protected BaseAction(BasePublisherService service) {
    this.service = service;
  }

  /**
   * Generate a message for publishing to GCP pub/sub metric topic.
   *
   * @param event the received message
   * @param processTime the simulated process time
   * @param publishTime publishTime of the received message
   * @return the generated message
   */
  public abstract T genMetricMessage(Event event, float processTime, Timestamp publishTime);

  /**
   * Get a Avro schema.
   *
   * @return Avro schema
   */
  public abstract Schema getSchema();

  /**
   * Get a receiver, which defines actions when receiving a message.
   *
   * @return the message receiver
   */
  public MessageReceiver getReceiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      try {
        logger.info("metric receive message: {}", PubSubUtil.getMessageData(message));
        T newMessage = processMessage(message, consumer);
        postProcessMessage(newMessage);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (IOException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Process a received message and generate an output message.
   *
   * @param message the received message
   * @param consumer the consumer
   * @return the output message
   */
  public final T processMessage(PubsubMessage message, AckReplyConsumer consumer)
      throws InterruptedException, IOException {
    logger.info("process received message, message: {}", PubSubUtil.getMessageData(message));
    float processTime = genProcessTime();
    Thread.sleep((long) (processTime * 1000));
    consumerAckOrNack(consumer);
    Event event = MessageUtil.convertToAvroEvent(message);
    return genMetricMessage(event, processTime, message.getPublishTime());
  }

  /**
   * Do after processing a message.
   *
   * @param newMessage the output message
   */
  public void postProcessMessage(T newMessage)
      throws IOException, InterruptedException, ExecutionException {
    service.publishMsg(MessageUtil.convertToPubSubMessage(newMessage, getSchema()));
  }

  /**
   * Ack or nack a message.
   *
   * @param consumer the consumer
   */
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    logger.info("consumerAckOrNack: ack");
    consumer.ack();
  }

  /**
   * Generate a common message.
   *
   * @param event the received message
   * @param processTime the simulated process time
   * @param publishTime publishTime of the received message
   * @return the common message
   */
  public final MetricsComplete genCommonMetricMessage(
      Event event, float processTime, Timestamp publishTime) {
    MetricsComplete metricMessage = new MetricsComplete();
    BeanUtils.copyProperties(event, metricMessage);
    metricMessage.setEventTimestamp(event.getSessionEndTime());
    metricMessage.setPublishTimestamp(Instant.ofEpochSecond(publishTime.getSeconds()));
    metricMessage.setProcessingTimeSec(PubSubUtil.formatFloat(processTime));
    metricMessage.setAckTimestamp(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
    float diffInHour = PubSubUtil.getDiffTimeInHour(event.getSessionEndTime(), event.getSessionStartTime());
    metricMessage.setSessionDurationHr(diffInHour);
    return metricMessage;
  }

  /**
   * Generate random process time.
   *
   * @return random process time
   */
  private float genProcessTime() {
    float ratio = PubSubUtil.genRandomFloat(0, 100);
    if (ratio <= 0.1) {
      return PubSubUtil.genRandomFloat(0.1f, 5);
    } else {
      return PubSubUtil.genRandomFloat(0.1f, 0.3f);
    }
  }
}
