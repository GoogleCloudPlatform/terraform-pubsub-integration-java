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
package com.googlecodesamples.cloud.jss.common.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.common.config.BasePublisherConfig;
import java.io.IOException;

/** Base factory class for creating a {@link com.google.cloud.pubsub.v1.Publisher} instance */
public abstract class BasePublisherFactory<T extends BasePublisherConfig> {

  private T config;

  protected abstract BatchingSettings getBatchSettings();

  protected abstract ExecutorProvider getExecutorProvider();

  protected abstract RetrySettings getRetrySetting();

  public T getConfig() {
    return config;
  }

  public void setConfig(T config) {
    this.config = config;
  }

  protected final Publisher newInstance() throws IOException {
    Publisher.Builder builder = Publisher.newBuilder(getConfig().getTopicName());
    BatchingSettings batchSettings = getBatchSettings();
    if (batchSettings != null) {
      builder.setBatchingSettings(batchSettings);
    }

    ExecutorProvider executorProvider = getExecutorProvider();
    if (executorProvider != null) {
      builder.setExecutorProvider(executorProvider);
    }

    RetrySettings retrySettings = getRetrySetting();
    if (retrySettings != null) {
      builder.setRetrySettings(retrySettings);
    }
    return builder.build();
  }
}
