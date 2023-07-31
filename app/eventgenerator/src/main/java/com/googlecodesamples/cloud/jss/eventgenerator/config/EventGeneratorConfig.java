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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configurations for the event generator, which generates randomized event messages. */
@Configuration
@ConfigurationProperties(prefix = "event.generator")
public class EventGeneratorConfig {

  private static final String ERROR_MSG_NEGATIVE_THREADS =
      "The threads for event generator should be greater than zero";

  private static final String ERROR_MSG_NEGATIVE_RUNTIME =
      "The runtime for event generator should be greater than zero";

  private Integer threads;

  private Float runtime;

  public Integer getThreads() {
    return threads;
  }

  /**
   * Set the number of threads for the event generator.
   *
   * @param threads maximum number of threads
   * @throws IllegalArgumentException if the number of threads is less than or equal to zero
   */
  public void setThreads(Integer threads) throws IllegalArgumentException {
    if (threads <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_THREADS);
    }
    this.threads = threads;
  }

  public Float getRuntime() {
    return runtime;
  }

  /**
   * Set the runtime for the event generator in minutes.
   *
   * @param runtime maximum runtime in minutes
   * @throws IllegalArgumentException if the runtime is less than or equal to zero
   */
  public void setRuntime(Float runtime) throws IllegalArgumentException {
    if (runtime <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_RUNTIME);
    }
    this.runtime = runtime;
  }
}
