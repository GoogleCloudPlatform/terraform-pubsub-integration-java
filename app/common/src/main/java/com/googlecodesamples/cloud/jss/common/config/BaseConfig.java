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
package com.googlecodesamples.cloud.jss.common.config;

import org.springframework.util.StringUtils;

/** Base configurations for GCP publisher/subscriber. */
public abstract class BaseConfig {

  private static final String ERROR_MSG_EMPTY_INPUT = "The topic/subscription name should not be empty";

  private static final String ERROR_MSG_NEGATIVE_THREADS = "The number of executor threads should be greater than zero";

  protected Integer executorThreads;

  protected Long outstandingMessages;

  public abstract String getInfo();

  public int getExecutorThreads() {
    return executorThreads;
  }

  public void setExecutorThreads(int executorThreads) throws IllegalArgumentException {
    if (executorThreads <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_THREADS);
    }
    this.executorThreads = executorThreads;
  }

  public Long getOutstandingMessages() {
    return outstandingMessages;
  }

  public void setOutstandingMessages(Long outstandingMessages) {
    this.outstandingMessages = outstandingMessages;
  }

  protected void checkEmptyName(String queueName) throws IllegalArgumentException {
    if (!StringUtils.hasText(queueName)) {
      throw new IllegalArgumentException(ERROR_MSG_EMPTY_INPUT);
    }
  }

  @Override
  public String toString() {
    return getInfo();
  }
}
