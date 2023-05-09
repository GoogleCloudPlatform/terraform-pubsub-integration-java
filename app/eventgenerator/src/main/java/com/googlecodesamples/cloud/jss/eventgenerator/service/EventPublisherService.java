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
import com.googlecodesamples.cloud.jss.eventgenerator.task.MessageTask;
import com.googlecodesamples.cloud.jss.eventgenerator.task.TimeoutTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EventPublisherService extends BasePublisherService {
  private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
  private ExecutorService executor = null;
  private Timer timer = null;

  public EventPublisherService(Publisher publisher) {
    super(publisher);
  }

  @Override
  public void init() {
    logger.info("initializing EventPublisherService");
  }

  /**
   * Publish random messages asynchronously.
   *
   * @param times         number of message each thread publish
   * @param thread        number of thread
   * @param sleep         time to sleep after each message (in second)
   * @param executionTime time to execute the task (in minute)
   */
  public void publishMsgAsync(int times, int thread, float sleep, float executionTime) {
    logger.info("settings for publishMsgAsync() times: {}, thread: {}, sleep: {}, executionTime: {}",
            times, thread, sleep, executionTime);

    if (times == MessageTask.INFINITE_FLAG && executionTime > 0) {
      timer = new Timer();
      timer.schedule(new TimeoutTask(this), (long) (executionTime * 60 * 1000));
    }

    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new MessageTask(this, sleep, times));
    }
  }

  /**
   * Shutdown the threadPool and timer.
   */
  @Override
  public void shutdown() {
    logger.info("shutting down the thread pool and timer for EventPublisherService");
    if (timer != null) {
      timer.cancel();
    }
    if (executor == null) {
      return;
    }
    try {
      executor.shutdown();
      if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }
}
