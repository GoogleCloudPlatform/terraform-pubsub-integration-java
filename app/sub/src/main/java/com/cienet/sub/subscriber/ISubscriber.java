package com.cienet.sub.subscriber;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;

public interface ISubscriber {
  void pull(BasicAcknowledgeablePubsubMessage basicMessage);

  void subscribe();
}
