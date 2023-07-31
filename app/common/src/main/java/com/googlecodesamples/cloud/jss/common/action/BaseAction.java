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
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base class for metric processing actions. */
public abstract class BaseAction<T> implements Action<T> {

  private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

  private BasePublisherService service;

  public BasePublisherService getService() {
    return service;
  }

  public void setService(BasePublisherService service) {
    this.service = service;
  }

  /**
   * Retrieve the Avro schema instance.
   *
   * @return Avro schema
   */
  public abstract Schema getSchema();

  /**
   * This method defines the action to be taken when a message is received.
   *
   * @param consumer the consumer that will be used to send an acknowledgement
   * @param message the received message to be processed
   * @param processTime the time taken to process the message
   * @param publishTime the time when the message was published
   * @return the ack message to be published
   * @throws IOException if the message cannot be converted to an Avro object
   */
  public abstract T respond(
      AckReplyConsumer consumer, PubsubMessage message, float processTime, Timestamp publishTime)
      throws IOException;

  /**
   * This method defines the action to be taken after the message has been processed.
   *
   * @param newMessage the ack message to be processed
   * @throws IOException if the newMessage cannot be converted to Cloud Pub/Sub compatible format.
   * @throws InterruptedException if the current thread was interrupted while sending the message.
   * @throws ExecutionException if the computation in {@link com.google.api.core.ApiFuture#get()}
   * threw an exception
   */
  public abstract void postProcess(T newMessage)
      throws IOException, InterruptedException, ExecutionException;

  /**
   * Retrieve the MessageReceiver instance, which defines the actions to be taken when a message is
   * received.
   *
   * @return the message receiver
   */
  public final MessageReceiver getReceiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      try {
        logger.info("metric receive message: {}", PubSubUtil.getMessageData(message));
        T newMessage = process(message, consumer);
        postProcess(newMessage);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (IOException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Process the Cloud Pub/Sub message and generate an output message by calling the
   * {@link #respond(AckReplyConsumer, PubsubMessage, float, Timestamp)} method.
   *
   * @param message the received to be processed
   * @param consumer the consumer that will be used to send an acknowledgement
   * @return the output message
   */
  public final T process(PubsubMessage message, AckReplyConsumer consumer)
      throws InterruptedException, IOException {
    logger.info("process received message, message: {}", PubSubUtil.getMessageData(message));
    // Sleep for a random amount to simulate the processing time.
    float processTime = PubSubUtil.genProcessTime();
    Thread.sleep((long) (processTime * 1000));
    return respond(consumer, message, processTime, message.getPublishTime());
  }
}
