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
package com.googlecodesamples.cloud.jss.eventgenerator.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.common.factory.BasePublisherFactory;
import com.googlecodesamples.cloud.jss.eventgenerator.config.EventPublisherConfig;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

/** Factory for creating an event {@link com.google.cloud.pubsub.v1.Publisher} instance. */
@Component
public class EventPublisherFactory extends BasePublisherFactory<EventPublisherConfig> {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherFactory.class);

  public EventPublisherFactory(EventPublisherConfig config) {
    setConfig(config);
  }

  /**
   * Creates a message publisher.
   *
   * @return publisher
   */
  @Bean
  public Publisher createPublisher() throws IOException {
    logger.info("eventPublisherConfig: {}", getConfig());
    return newInstance();
  }

  @Override
  protected BatchingSettings getBatchSettings() {
    FlowControlSettings flowControlSettings =
        FlowControlSettings.newBuilder()
            .setMaxOutstandingElementCount(getConfig().getOutstandingMessages())
            .setMaxOutstandingRequestBytes(Long.MAX_VALUE)
            .build();
    return BatchingSettings.newBuilder()
        .setFlowControlSettings(flowControlSettings)
        .setElementCountThreshold(getConfig().getBatchSize())
        .build();
  }

  @Override
  protected ExecutorProvider getExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(getConfig().getExecutorThreads())
        .build();
  }

  @Override
  protected RetrySettings getRetrySetting() {
    return RetrySettings.newBuilder()
        .setInitialRpcTimeout(Duration.ofSeconds(getConfig().getInitialTimeout()))
        .setMaxRpcTimeout(Duration.ofSeconds(getConfig().getTotalTimeout()))
        .setTotalTimeout(Duration.ofSeconds(getConfig().getTotalTimeout()))
        .build();
  }
}
