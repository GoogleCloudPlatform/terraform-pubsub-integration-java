package com.cienet.pub.publisher;

import com.cienet.pub.utilities.EvChargeEvent;

public interface IPublisher {
  void publish(EvChargeEvent evChargeEvent);
}
