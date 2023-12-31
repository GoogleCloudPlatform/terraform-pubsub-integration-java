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
package com.googlecodesamples.cloud.jss.eventgenerator.config;

import com.googlecodesamples.cloud.jss.common.config.BasePublisherConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configurations for the event {@link com.google.cloud.pubsub.v1.Publisher}. */
@Configuration
@ConfigurationProperties(prefix = "event.publisher")
public class EventPublisherConfig extends BasePublisherConfig {
  @Override
  public String getInfo() {
    return String.format(
        "topic name: %s, flow Control outStanding message: %d, executor threads: %d, initial"
            + " timeout: %d, total timeout %d, batch size: %d",
        getTopicName(),
        getOutstandingMessages(),
        getExecutorThreads(),
        getInitialTimeout(),
        getTotalTimeout(),
        getBatchSize());
  }
}
