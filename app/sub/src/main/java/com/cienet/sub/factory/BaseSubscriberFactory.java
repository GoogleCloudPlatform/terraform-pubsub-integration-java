package com.cienet.sub.factory;

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

  @Value("${max.outstanding.request.megabyte}")
  private int maxMegaByte;

  @Value("${max.outstanding.element.count}")
  private long maxElementCount;

  @Value("${parallel.pull.count}")
  private int parallelPullCount;

  @Value("${sub.thread.count}")
  private int subThreadCount;

  public BaseSubscriberFactory() {}

  public Subscriber createSubscriber(MessageReceiver receiver) {
    log.info("create defaultSubscriber, listen to " + eventSubscription);
    return Subscriber.newBuilder(eventSubscription, receiver)
        .setFlowControlSettings(customFlowControlSetting())
        .setExecutorProvider(customExecutorProvider())
        .setParallelPullCount(parallelPullCount)
        .build();
  }

  public void destroySubscriber(Subscriber subscriber) {
    subscriber.stopAsync();
  }

  private FlowControlSettings customFlowControlSetting() {
    return FlowControlSettings.newBuilder()
        .setMaxOutstandingElementCount(maxElementCount)
        .setMaxOutstandingRequestBytes(maxMegaByte * 1024L * 1024L)
        .build();
  }

  private ExecutorProvider customExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(subThreadCount)
        .build();
  }
}
