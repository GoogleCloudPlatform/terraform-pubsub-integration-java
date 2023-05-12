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
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/** Base class for metric processing actions. */
public abstract class BaseAction<T> {

  private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

  protected final BasePublisherService service;

  public BaseAction(BasePublisherService service) {
    this.service = service;
  }

  /**
   * Generate a GCP Pub/Sub message for the metric topic.
   *
   * @param event the received message
   * @param processTime the simulated process time
   * @param publishTime publishTime of the received message
   * @return the generated message
   */
  public abstract T genMetricMessage(Event event, float processTime, Timestamp publishTime);

  /**
   * Retrieve the Avro schema instance.
   *
   * @return Avro schema
   */
  public abstract Schema getSchema();

  /**
   * Retrieve the MessageReceiver instance, which defines the actions to be taken when a message is received.
   *
   * @return the message receiver
   */
  public final MessageReceiver getReceiver() {
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
   * Process the GCP Pub/Sub message and generate an output message.
   *
   * @param message the received message
   * @param consumer the consumer
   * @return the output message
   */
  private T processMessage(PubsubMessage message, AckReplyConsumer consumer)
      throws InterruptedException, IOException {
    logger.info("process received message, message: {}", PubSubUtil.getMessageData(message));
    float processTime = PubSubUtil.genProcessTime();
    Thread.sleep((long) (processTime * 1000));
    consumerAckOrNack(consumer);
    Event event = MessageUtil.convertToAvroEvent(message);
    return genMetricMessage(event, processTime, message.getPublishTime());
  }

  /**
   * Post-action for the output message.
   *
   * @param newMessage the output message
   */
  public void postProcessMessage(T newMessage)
      throws IOException, InterruptedException, ExecutionException {
    service.publishMsg(MessageUtil.convertToPubSubMessage(newMessage, getSchema()));
  }

  /**
   * Generate an ACK or NACK response for the message.
   *
   * @param consumer the consumer
   */
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    logger.info("consumerAckOrNack: ack");
    consumer.ack();
  }

  /**
   * Generate a MetricsComplete message.
   *
   * @param event the received message
   * @param processTime the simulated process time
   * @param publishTime publishTime of the received message
   * @return the common message
   */
  public final MetricsComplete genCommonMetricMessage(
      Event event, float processTime, Timestamp publishTime) {
    MetricsComplete message = new MetricsComplete();
    BeanUtils.copyProperties(event, message);
    message.setEventTimestamp(event.getSessionEndTime());
    message.setPublishTimestamp(Instant.ofEpochSecond(publishTime.getSeconds()));
    message.setProcessingTimeSec(PubSubUtil.formatFloat(processTime));
    message.setAckTimestamp(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
    float diffInHour =
        PubSubUtil.getDiffTimeInHour(event.getSessionEndTime(), event.getSessionStartTime());
    message.setSessionDurationHr(diffInHour);
    return message;
  }
}
