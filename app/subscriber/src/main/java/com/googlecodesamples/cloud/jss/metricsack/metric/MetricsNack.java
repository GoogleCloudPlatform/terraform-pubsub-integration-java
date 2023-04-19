package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricNack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsNack extends Metric<EvChargeMetricNack> {
  private static final Logger log = LoggerFactory.getLogger(MetricsNack.class);

  protected MetricsNack(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }

  @Override
  public void messageAckOrNack(ConvertedBasicAcknowledgeablePubsubMessage<EvChargeEvent> message) {
    log.info("MessageAckOrNack nack");
    message.nack(); // nack every message receives 🐞
  }

  @Override
  public EvChargeMetricNack genMetricMessage(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricNack metricMessage = new EvChargeMetricNack();
    EvChargeMetricComplete commonMetricMessage = genCommonMetricMessage(evChargeEvent, processTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }
}
