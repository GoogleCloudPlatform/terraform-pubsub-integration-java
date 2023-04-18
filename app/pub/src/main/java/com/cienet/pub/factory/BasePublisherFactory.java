package com.cienet.pub.factory;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Publisher;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

@Component
public class BasePublisherFactory {
  private static final Logger log = LoggerFactory.getLogger(BasePublisherFactory.class);

  @Value("${event.topic}")
  private String eventTopic;

  @Value("${max.outstanding.request.megabyte}")
  private int maxMegaByte;

  @Value("${max.outstanding.element.count}")
  private long maxElementCount;

  @Value("${initial.rpc.timeout}")
  private int initialTimeout;

  @Value("${max.total.timeout}")
  private int maxTotalTimeout;

  @Value("${element.count.threshold}")
  private long elementCountThreshold;

  @Value("${request.byte.threshold}")
  private long requestByteThreshold;

  @Value("${delay.threshold}")
  private int delayThreshold;

  @Bean(name = "defaultPublisher")
  public Publisher createPublisher() throws IOException {
    log.info("create defaultPublisher, publish to " + eventTopic);
    return Publisher.newBuilder(eventTopic)
        .setBatchingSettings(customBatchSetting())
        .setRetrySettings(customRetrySetting())
        .build();
  }

  public void destroyPublisher(Publisher publisher) throws InterruptedException {
    if (publisher != null) {
      publisher.shutdown();
      publisher.awaitTermination(1, TimeUnit.MINUTES);
    }
  }

  private BatchingSettings customBatchSetting() {
    FlowControlSettings flowControlSettings =
        FlowControlSettings.newBuilder()
            .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
            .setMaxOutstandingRequestBytes(maxMegaByte * 1024 * 1024L) // 10 MiB
            .setMaxOutstandingElementCount(maxElementCount) // 100 messages
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
        .setInitialRpcTimeout(Duration.ofSeconds(initialTimeout))
        .setMaxRpcTimeout(
            Duration.ofSeconds(
                initialTimeout)) // max rpc timeout must not be shorter than initial timeout
        .setTotalTimeout(Duration.ofSeconds(maxTotalTimeout))
        .build();
  }
}
