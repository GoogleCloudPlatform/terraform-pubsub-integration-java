package com.googlecodesamples.cloud.jss.metricsack.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.metricsack.config.PublishConfig;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BasePublisherFactory {
  private static final Logger log = LoggerFactory.getLogger(BasePublisherFactory.class);
  private final PublishConfig publishConfig;

  public BasePublisherFactory(PublishConfig publishConfig) {
    this.publishConfig = publishConfig;
  }

  @Bean
  public Publisher createPublisher() throws IOException {
    log.info("Create publisher [{}]", publishConfig);
    return Publisher.newBuilder(publishConfig.getMetricsTopic())
        .setBatchingSettings(customBatchSetting())
        .setExecutorProvider(customExecutorProvider())
        .build();
  }

  private BatchingSettings customBatchSetting() {
    return BatchingSettings.newBuilder()
        .setElementCountThreshold(publishConfig.getBatchSize())
        .build();
  }

  private ExecutorProvider customExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(publishConfig.getExecutorThreads())
        .build();
  }
}
