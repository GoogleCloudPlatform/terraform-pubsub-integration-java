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

/** Base configurations for GCP subscriber. */
public abstract class BaseSubscriberConfig extends BaseConfig {

  private String eventSubscription;

  private Integer parallelPull;

  public String getEventSubscription() {
    return eventSubscription;
  }

  public void setEventSubscription(String eventSubscription) throws IllegalArgumentException {
    checkEmptyName(eventSubscription);
    this.eventSubscription = eventSubscription;
  }

  public Integer getParallelPull() {
    return parallelPull;
  }

  public void setParallelPull(Integer parallelPull) {
    this.parallelPull = parallelPull;
  }
}
