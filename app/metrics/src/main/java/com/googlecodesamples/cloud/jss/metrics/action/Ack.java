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
package com.googlecodesamples.cloud.jss.metrics.action;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.generated.MetricsAck;
import com.googlecodesamples.cloud.jss.common.util.MessageUtil;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

/** MetricAck specified actions. */
@Component
@ConditionalOnProperty(name = "metric.app.type", havingValue = PubSubConst.METRICS_ACK)
public class Ack extends BaseAction<MetricsAck> {

  private static final Logger logger = LoggerFactory.getLogger(Ack.class);

  public Ack(MetricPublisherService service) {
    setService(service);
  }

  /**
   * Retrieve the schema of the MetricsAck class.
   *
   * @return the schema of the MetricsAck class
   */
  @Override
  public Schema getSchema() {
    return MetricsAck.getClassSchema();
  }

  /**
   * Invokes the {@link com.google.cloud.pubsub.v1.AckReplyConsumer#ack()} to acknowledge the
   * message, and then generates the ack message of type {@link MetricsAck} to be published.
   *
   * @param consumer the consumer that will be used to send an acknowledgement
   * @param message the received message to be processed
   * @param processTime the time taken to process the message
   * @param publishTime the time when the message was published
   * @return the ack message to be published
   * @throws IOException if the message cannot be converted to an Avro object
   */
  @Override
  public MetricsAck respond(
      AckReplyConsumer consumer, PubsubMessage message, float processTime, Timestamp publishTime)
      throws IOException {
    logger.info("consumer response: ACK");
    consumer.ack();
    Event event = MessageUtil.convertToAvroEvent(message);
    return genAckMessage(event, processTime, publishTime);
  }

  /**
   * Converts the ack message of type {@link MetricsAck} to a Cloud Pub/Sub
   * compatible format, and then publishes it using the {@link MetricPublisherService}.<br><br>
   *
   * To change the default settings for the "metric publisher", check the following configurations in
   * application.properties file: <br>
   * <li> Modify the "metric.publisher.topic_name" for the topic name.
   * <li> Modify the "metric.publisher.executor_threads" for the number of publisher threads.
   * <li> Modify the "metric.publisher.batch_size" for the number of messages to batch.
   *
   * @param newMessage the ack message to be processed
   * @throws IOException if the newMessage cannot be converted to Cloud Pub/Sub compatible format.
   * @throws InterruptedException if the current thread was interrupted while sending the message.
   * @throws ExecutionException if the computation in {@link com.google.api.core.ApiFuture#get()}
   * threw an exception
   */
  @Override
  public void postProcess(MetricsAck newMessage)
      throws IOException, InterruptedException, ExecutionException {
    getService().publishMsg(MessageUtil.convertToPubSubMessage(newMessage, getSchema()));
  }

  /**
   * Generates an ack message of type {@link MetricsAck} from the event.
   *
   * @param event the event received from the Cloud Pub/Sub subscription
   * @param processTime the time taken to process the message
   * @param publishTime the time when the message was published
   * @return the ack message to be published
   */
  private MetricsAck genAckMessage(Event event, float processTime, Timestamp publishTime) {
    logger.info("event: {}, processTime {}, publishTime {}", event, processTime, publishTime);

    MetricsAck message = new MetricsAck();
    // Copy common attributes from event.
    BeanUtils.copyProperties(event, message);
    Instant startTime = event.getSessionStartTime();
    Instant endTime = event.getSessionEndTime();

    // Set additional attributes for ack message.
    message.setEventTimestamp(endTime);
    message.setPublishTimestamp(Instant.ofEpochSecond(publishTime.getSeconds()));
    message.setProcessingTimeSec(PubSubUtil.formatFloat(processTime));
    message.setAckTimestamp(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
    message.setSessionDurationHr(PubSubUtil.getDiffTimeInHour(endTime, startTime));
    message.setMetricsNode(MessageUtil.getHostname());
    logger.info("generated metric ack message: {}", message);
    return message;
  }
}
