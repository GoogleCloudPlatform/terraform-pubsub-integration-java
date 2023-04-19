package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricAck;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsAck extends Metric<EvChargeMetricAck> {
  protected MetricsAck(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }

  @Override
  public EvChargeMetricAck genMetricMessage(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricAck metricMessage = new EvChargeMetricAck();
    EvChargeMetricComplete commonMetricMessage = genCommonMetricMessage(evChargeEvent, processTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }
}
