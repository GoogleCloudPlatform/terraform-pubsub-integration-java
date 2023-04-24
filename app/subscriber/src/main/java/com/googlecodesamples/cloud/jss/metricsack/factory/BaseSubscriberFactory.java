package com.googlecodesamples.cloud.jss.metricsack.factory;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.googlecodesamples.cloud.jss.metricsack.config.SubscribeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaseSubscriberFactory {
  private static final Logger log = LoggerFactory.getLogger(BaseSubscriberFactory.class);
  private final SubscribeConfig subscribeConfig;

  public BaseSubscriberFactory(SubscribeConfig subscribeConfig) {
    this.subscribeConfig = subscribeConfig;
  }

  public Subscriber createSubscriber(MessageReceiver receiver) {
    log.info("Create subscriber [{}]", subscribeConfig);
    return Subscriber.newBuilder(subscribeConfig.getEventSubscription(), receiver)
        .setFlowControlSettings(customFlowControlSetting())
        .setExecutorProvider(customExecutorProvider())
        .setParallelPullCount(subscribeConfig.getParallelPull())
        .build();
  }

  private FlowControlSettings customFlowControlSetting() {
    return FlowControlSettings.newBuilder()
        .setMaxOutstandingElementCount(subscribeConfig.getOutstandingMessages())
        .build();
  }

  private ExecutorProvider customExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(subscribeConfig.getExecutorThreads())
        .build();
  }
}
