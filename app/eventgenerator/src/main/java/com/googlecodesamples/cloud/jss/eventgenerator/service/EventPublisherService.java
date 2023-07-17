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
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import com.googlecodesamples.cloud.jss.eventgenerator.config.EventGeneratorConfig;
import com.googlecodesamples.cloud.jss.eventgenerator.task.MessageTask;
import com.googlecodesamples.cloud.jss.eventgenerator.task.TimeoutTask;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Backend service controller to asynchronously publish messages to Cloud Pub/Sub. */
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
    publishMsgAsync(config.getThreads(), config.getRuntime());
  }

  /**
   * Publish random messages asynchronously.
   *
   * @param threads number of thread
   * @param runtime time to execute the task (in minute)
   */
  public synchronized void publishMsgAsync(int threads, float runtime) {
    if ((executor != null && !executor.isTerminated()) || timer != null) {
      logger.warn("thread pool or timer already exist");
      return;
    }

    logger.info("settings for publishMsgAsync() threads: {}, runtime: {}", threads, runtime);

    if (runtime > 0) {
      // Start a timer to shut down the thread pool when runtime is up.
      timer = new Timer();
      timer.schedule(new TimeoutTask(this), (long) (runtime * 60 * 1000));
    }

    // Start a thread pool to execute the defined tasks.
    executor = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
      executor.execute(new MessageTask(this));
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

    // Shut down the thread pool and timer and reset the number of message received.
    try {
      executor.shutdownNow();
    } finally {
      closeTimer();
      resetTotalMessage();
    }
  }

  private void closeTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}
