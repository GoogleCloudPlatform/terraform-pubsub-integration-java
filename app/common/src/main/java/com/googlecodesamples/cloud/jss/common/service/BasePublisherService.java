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
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base service controller for publisher. */
public abstract class BasePublisherService {

  private static final Logger logger = LoggerFactory.getLogger(BasePublisherService.class);

  private final AtomicInteger totalMessage;

  private final Publisher publisher;

  public BasePublisherService(Publisher publisher) {
    this.totalMessage = new AtomicInteger(PubSubConst.INITIAL_TOTAL_MESSAGE);
    this.publisher = publisher;
    init();
  }

  public Publisher getPublisher() {
    return publisher;
  }

  public void resetTotalMessage() {
    totalMessage.set(PubSubConst.INITIAL_TOTAL_MESSAGE);
  }

  public abstract void init();

  public abstract void shutdown();

  /**
   * Publish a message to GCP pub/sub topic.
   *
   * @param message message to be published
   */
  public void publishMsg(PubsubMessage message) throws InterruptedException, ExecutionException {
    int messageCount = totalMessage.incrementAndGet();
    String threadName = Thread.currentThread().getName();
    String topicName = publisher.getTopicName().toString();
    long publishTime = System.currentTimeMillis();

    logger.info("thread: {}, topic: {}, messageCount: {}", threadName, topicName, messageCount);

    ApiFuture<String> future = publisher.publish(message);
    String result = future.get();
    logger.info("message: {}, callback received: {}", PubSubUtil.getMessageData(message), result);

    long callBackTime = System.currentTimeMillis();
    logger.info("message: {}, process time: {}", result, (callBackTime - publishTime));
  }

  /** Shutdown and release resources. */
  @PreDestroy
  public void cleanUp() {
    logger.info("shutdown publisher");
    publisher.shutdown();
    shutdown();
  }
}
