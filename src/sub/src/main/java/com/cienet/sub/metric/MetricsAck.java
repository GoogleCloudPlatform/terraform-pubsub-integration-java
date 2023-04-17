package com.cienet.sub.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

@Component
public class MetricsAck extends Metric {
  protected MetricsAck(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }
}
