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

package com.googlecodesamples.cloud.jss.metrics.service;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.googlecodesamples.cloud.jss.metrics.factory.EventSubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/** Backend service controller to process event messages from Cloud Pub/Sub. */
@Service
public class EventSubscriberService {

  private static final Logger logger = LoggerFactory.getLogger(EventSubscriberService.class);

  private final EventSubscriberFactory factory;
  private Subscriber subscriber;

  @Value("${metric.app.type}")
  private String metricAppType;

  public EventSubscriberService(EventSubscriberFactory factory) {
    this.factory = factory;
  }

  /** Start pulling messages automatically when the application is started. <br><br>
   * To change the default settings for the "event subscriber", check the following configurations in
   * application.properties file: <br>
   * <li> Modify the "event.subscriber.event_subscription" for the subscription name.
   * <li> Modify the "event.subscriber.parallel_pull" for the settings of parallel pull.
   * <li> Modify the "event.subscriber.outstanding_messages" for the number of outstanding messages.
   * <li> Modify the "event.subscriber.executor_threads" for the number of subscriber threads.
   *
   * @return the {@link Subscriber} instance
   */
  @PostConstruct
  public Subscriber startSubscriberAsync() {
    logger.info("metric app type: {}", metricAppType);
    subscriber = factory.createSubscriber();

    // Listen for unrecoverable failures. Recreate subscriber and restart subscribing.
    subscriber.addListener(
        new Subscriber.Listener() {
          public void failed(Subscriber.State from, Throwable failure) {
            cleanUp();
            if (!factory.getProvider().getExecutor().isShutdown()) {
              startSubscriberAsync();
            }
          }
        },
        MoreExecutors.directExecutor());

    // Start the subscriber to receive messages.
    subscriber.startAsync().awaitRunning();
    return subscriber;
  }

  /** Stop pulling messages and release resources. */
  @PreDestroy
  public void cleanUp() {
    if (subscriber != null) {
      subscriber.stopAsync();
    }
  }
}
