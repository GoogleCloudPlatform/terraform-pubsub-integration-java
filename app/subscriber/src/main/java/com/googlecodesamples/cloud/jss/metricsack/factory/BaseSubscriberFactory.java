package com.googlecodesamples.cloud.jss.metricsack.factory;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseSubscriberFactory {
  private static final Logger log = LoggerFactory.getLogger(BaseSubscriberFactory.class);

  @Value("${event.subscription}")
  private String eventSubscription;

  @Value("${flow.control.max.outstanding.request}")
  private int maxOutstandingRequest;

  @Value("${flow.control.max.outstanding.element}")
  private long maxOutstandingElement;

  @Value("${subscriber.executor.threads}")
  private int subscriberExecutorThreads;

  @Value("${parallel.pull.count}")
  private int parallelPullCount;

  public Subscriber createSubscriber(MessageReceiver receiver) {
    log.info("Create subscriber to subscription [{}]", eventSubscription);
    return Subscriber.newBuilder(eventSubscription, receiver)
        .setFlowControlSettings(customFlowControlSetting())
        .setExecutorProvider(customExecutorProvider())
        .setParallelPullCount(parallelPullCount)
        .build();
  }

  private FlowControlSettings customFlowControlSetting() {
    return FlowControlSettings.newBuilder()
        .setMaxOutstandingElementCount(maxOutstandingElement)
        .setMaxOutstandingRequestBytes(maxOutstandingRequest * 1024 * 1024L)
        .build();
  }

  private ExecutorProvider customExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(subscriberExecutorThreads)
        .build();
  }
}
