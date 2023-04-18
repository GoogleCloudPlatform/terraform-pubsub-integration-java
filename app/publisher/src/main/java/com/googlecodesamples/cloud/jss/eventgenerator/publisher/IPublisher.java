package com.googlecodesamples.cloud.jss.eventgenerator.publisher;

import com.googlecodesamples.cloud.jss.eventgenerator.utilities.EvChargeEvent;

public interface IPublisher {
  void publish(EvChargeEvent evChargeEvent);
}
