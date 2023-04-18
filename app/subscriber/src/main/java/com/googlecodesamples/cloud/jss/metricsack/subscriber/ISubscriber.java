package com.googlecodesamples.cloud.jss.metricsack.subscriber;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;

public interface ISubscriber {
  void pull(BasicAcknowledgeablePubsubMessage basicMessage);

  void subscribe();
}
