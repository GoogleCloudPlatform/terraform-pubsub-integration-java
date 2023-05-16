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
package com.googlecodesamples.cloud.jss.eventgenerator.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import com.googlecodesamples.cloud.jss.eventgenerator.config.EventGeneratorConfig;
import com.googlecodesamples.cloud.jss.eventgenerator.task.MessageTask;
import com.googlecodesamples.cloud.jss.eventgenerator.task.TimeoutTask;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Backend service controller to asynchronously publish messages to GCP Pub/Sub. */
@Service
public class EventPublisherService extends BasePublisherService {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);

  private final EventGeneratorConfig config;

  private ExecutorService executor = null;

  private Timer timer = null;

  public EventPublisherService(Publisher publisher, EventGeneratorConfig config) {
    super(publisher);
    this.config = config;
  }

  @Override
  public void init() {
    logger.info("initializing EventPublisherService");
  }

  @PostConstruct
  public void startPublishMsgAsync() {
    publishMsgAsync(
        PubSubConst.INFINITE_FLAG, config.getThreads(), config.getSleepTime(), config.getRuntime());
  }

  /**
   * Publish random messages asynchronously.
   *
   * @param times number of message each thread publish
   * @param thread number of thread
   * @param sleep time to sleep after each message (in second)
   * @param executionTime time to execute the task (in minute)
   */
  public synchronized void publishMsgAsync(
      int times, int thread, float sleep, float executionTime) {
    if ((executor != null && !executor.isTerminated()) || timer != null) {
      logger.warn("thread pool or timer already exist");
      return;
    }

    logger.info(
        "settings for publishMsgAsync() times: {}, thread: {}, sleep: {}, executionTime: {}",
        times,
        thread,
        sleep,
        executionTime);

    if (times == PubSubConst.INFINITE_FLAG && executionTime > 0) {
      timer = new Timer();
      timer.schedule(new TimeoutTask(this), (long) (executionTime * 60 * 1000));
    }

    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new MessageTask(this, sleep, times));
    }
  }

  /** Shutdown the threadPool and timer. */
  @Override
  public synchronized void shutdown() {
    logger.info("shutting down the thread pool and timer for EventPublisherService");
    if (executor == null) {
      closeTimer();
      return;
    }

    try {
      executor.shutdown();
      if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      logger.error("thread pool interrupted", e);
    } finally {
      executor.shutdownNow();
      closeTimer();
    }
  }

  private void closeTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}
