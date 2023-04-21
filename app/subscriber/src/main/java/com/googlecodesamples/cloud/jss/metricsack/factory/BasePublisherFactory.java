package com.googlecodesamples.cloud.jss.metricsack.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Publisher;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

@Component
public class BasePublisherFactory {
  private static final Logger log = LoggerFactory.getLogger(BasePublisherFactory.class);

  @Value("${metric.topic}")
  private String metricTopic;

  @Value("${flow.control.max.outstanding.request}")
  private int maxOutstandingRequest;

  @Value("${flow.control.max.outstanding.element}")
  private long maxOutstandingElement;

  @Value("${retry.initial.rpc.timeout}")
  private int initialRpcTimeout;

  @Value("${retry.total.timeout}")
  private int totalTimeout;

  @Value("${retry.max.rpc.timeout}")
  private int maxRpcTimeout;

  @Value("${batch.element.count.threshold}")
  private long elementCountThreshold;

  @Value("${batch.request.byte.threshold}")
  private long requestByteThreshold;

  @Value("${batch.delay.threshold}")
  private int delayThreshold;

  @Value("${publisher.executor.threads}")
  private int publisherExecutorThreads;

  @Bean
  public Publisher createPublisher() throws IOException {
    log.info("Create publisher to topic [{}]", metricTopic);
    return Publisher.newBuilder(metricTopic)
        .setBatchingSettings(customBatchSetting())
        .setRetrySettings(customRetrySetting())
        .setExecutorProvider(customExecutorProvider())
        .build();
  }

  private BatchingSettings customBatchSetting() {
    FlowControlSettings flowControlSettings =
        FlowControlSettings.newBuilder()
            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
            .setMaxOutstandingRequestBytes(maxOutstandingRequest * 1024 * 1024L)
            .setMaxOutstandingElementCount(maxOutstandingElement)
            .build();
    return BatchingSettings.newBuilder()
        .setFlowControlSettings(flowControlSettings)
        .setElementCountThreshold(elementCountThreshold)
        .setRequestByteThreshold(requestByteThreshold)
        .setDelayThreshold(Duration.ofMillis(delayThreshold))
        .build();
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
        .setExecutorThreadCount(publisherExecutorThreads)
        .build();
  }
}
