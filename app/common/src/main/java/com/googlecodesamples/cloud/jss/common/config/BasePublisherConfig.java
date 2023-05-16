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

/** Base configurations for the publisher service. */
public abstract class BasePublisherConfig extends BaseConfig {

  private static final String ERROR_MSG_NEGATIVE_BATCH =
      "The batch size for publisher should be greater than zero";

  private String topicName;

  private Long batchSize;

  private Integer initialTimeout;

  private Integer totalTimeout;

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) throws IllegalArgumentException {
    checkEmptyName(topicName);
    this.topicName = topicName;
  }

  public Long getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Long batchSize) throws IllegalArgumentException {
    if (batchSize <= 0) {
      throw new IllegalArgumentException(ERROR_MSG_NEGATIVE_BATCH);
    }
    this.batchSize = batchSize;
  }

  public Integer getInitialTimeout() {
    return initialTimeout;
  }

  public void setInitialTimeout(Integer initialTimeout) {
    this.initialTimeout = initialTimeout;
  }

  public Integer getTotalTimeout() {
    return totalTimeout;
  }

  public void setTotalTimeout(Integer totalTimeout) {
    this.totalTimeout = totalTimeout;
  }
}
