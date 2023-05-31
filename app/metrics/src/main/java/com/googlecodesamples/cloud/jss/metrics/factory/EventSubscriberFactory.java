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

package com.googlecodesamples.cloud.jss.metrics.factory;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.factory.BaseSubscriberFactory;
import com.googlecodesamples.cloud.jss.metrics.config.EventSubscriberConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Factory for creating an event {@link com.google.cloud.pubsub.v1.Subscriber} instance. */
@Component
public class EventSubscriberFactory extends BaseSubscriberFactory<EventSubscriberConfig> {

  private static final Logger logger = LoggerFactory.getLogger(EventSubscriberFactory.class);

  public EventSubscriberFactory(EventSubscriberConfig config, BaseAction metric) {
    setConfig(config);
    setMetric(metric);
  }

  /**
   * Creates an event subscriber.
   *
   * @return subscriber
   */
  public Subscriber createSubscriber() {
    logger.info("eventSubscriberConfig: {}", getConfig());
    return newInstance(getMetric().getReceiver());
  }

  @Override
  protected FlowControlSettings getFlowControlSettings() {
    return FlowControlSettings.newBuilder()
        .setMaxOutstandingElementCount(getConfig().getOutstandingMessages())
        .build();
  }

  @Override
  protected ExecutorProvider getExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(getConfig().getExecutorThreads())
        .build();
  }
}
