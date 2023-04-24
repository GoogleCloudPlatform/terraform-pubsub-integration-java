package com.googlecodesamples.cloud.jss.eventgenerator.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.eventgenerator.config.PublishConfig;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

@Component
public class BasePublisherFactory {
  private static final Logger log = LoggerFactory.getLogger(BasePublisherFactory.class);
  private final int initialRpcTimeout = 5;
  private final int maxRpcTimeout = initialRpcTimeout;
  private final int totalTimeout = 600;
  private final PublishConfig publishConfig;

  public BasePublisherFactory(PublishConfig publishConfig) {
    this.publishConfig = publishConfig;
  }

  @Bean
  public Publisher createPublisher() throws IOException {
    String eventTopic = publishConfig.getEventTopic();
    log.info("Create publisher [{}]", publishConfig);
    return Publisher.newBuilder(eventTopic)
        .setBatchingSettings(customBatchSetting())
        .setRetrySettings(customRetrySetting())
        .setExecutorProvider(customExecutorProvider())
        .build();
  }

  private BatchingSettings customBatchSetting() {
    FlowControlSettings flowControlSettings =
        FlowControlSettings.newBuilder()
            .setMaxOutstandingElementCount(publishConfig.getOutstandingMessages())
            .setMaxOutstandingRequestBytes(Long.MAX_VALUE)
            .build();
    return BatchingSettings.newBuilder().setFlowControlSettings(flowControlSettings).build();
  }

  private RetrySettings customRetrySetting() {
    return RetrySettings.newBuilder()
        .setInitialRpcTimeout(Duration.ofSeconds(initialRpcTimeout))
        .setMaxRpcTimeout(Duration.ofSeconds(maxRpcTimeout))
        .setTotalTimeout(Duration.ofSeconds(totalTimeout))
        .build();
  }

  private ExecutorProvider customExecutorProvider() {
    return InstantiatingExecutorProvider.newBuilder()
        .setExecutorThreadCount(publishConfig.getExecutorThreads())
        .build();
  }
}
