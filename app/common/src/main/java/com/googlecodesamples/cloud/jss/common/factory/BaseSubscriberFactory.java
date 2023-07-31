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

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.config.BaseSubscriberConfig;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;

/** Base factory class for creating a {@link com.google.cloud.pubsub.v1.Subscriber} instance */
public abstract class BaseSubscriberFactory<T extends BaseSubscriberConfig> {

  private T config;

  private BaseAction metric;

  private ExecutorProvider provider;

  protected abstract FlowControlSettings getFlowControlSettings();

  protected abstract ExecutorProvider getExecutorProvider();

  public T getConfig() {
    return config;
  }

  public void setConfig(T config) {
    this.config = config;
  }

  public BaseAction getMetric() {
    return metric;
  }

  public void setMetric(BaseAction metric) {
    this.metric = metric;
  }

  public ExecutorProvider getProvider() {
    return provider;
  }

  public void setProvider(ExecutorProvider provider) {
    this.provider = provider;
  }

  /**
   * Create a new {@link Subscriber} instance using the provided configuration.
   *
   * @return a new {@link Subscriber} instance
   */
  protected Subscriber newInstance(MessageReceiver receiver) {
    ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(
            PubSubUtil.getEnvProjectId(), getConfig().getEventSubscription());
    Subscriber.Builder builder = Subscriber.newBuilder(subscriptionName.toString(), receiver);
    FlowControlSettings flowControlSettings = getFlowControlSettings();
    if (flowControlSettings != null) {
      builder.setFlowControlSettings(flowControlSettings);
    }

    provider = getExecutorProvider();
    if (provider != null) {
      builder.setExecutorProvider(provider);
    }

    Integer parallelPull = getConfig().getParallelPull();
    if (parallelPull != null) {
      builder.setParallelPullCount(parallelPull);
    }
    return builder.build();
  }
}
