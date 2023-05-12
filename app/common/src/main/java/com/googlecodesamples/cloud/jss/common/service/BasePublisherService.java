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
package com.googlecodesamples.cloud.jss.common.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import java.util.concurrent.ExecutionException;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base service controller for publisher. */
public abstract class BasePublisherService {

  private static final Logger logger = LoggerFactory.getLogger(BasePublisherService.class);

  private final Publisher publisher;

  public BasePublisherService(Publisher publisher) {
    this.publisher = publisher;
    init();
  }

  public Publisher getPublisher() {
    return publisher;
  }

  public abstract void init();

  public abstract void shutdown();

  /**
   * Publish a message to GCP pub/sub topic.
   *
   * @param message message to be published
   */
  public void publishMsg(PubsubMessage message) throws InterruptedException, ExecutionException {
    logger.info(
        "thread: {}, topic name: {}, message: {}",
        Thread.currentThread().getName(),
        publisher.getTopicName(),
        PubSubUtil.getMessageData(message));

    ApiFuture<String> messageId = publisher.publish(message);
    logger.info(
        "message id: {} callback received, message: {}",
        messageId.get(),
        PubSubUtil.getMessageData(message));
  }

  /** Shutdown and release resources. */
  @PreDestroy
  public void cleanUp() {
    shutdown();
  }
}
