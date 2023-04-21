package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricAck;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsAck extends Metric<EvChargeMetricAck> {
  private static final Logger log = LoggerFactory.getLogger(MetricsAck.class);

  protected MetricsAck(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public EvChargeMetricAck genMetricMessage(
      EvChargeEvent evChargeEvent, float processTime, Timestamp publishTime) {
    EvChargeMetricAck metricMessage = new EvChargeMetricAck();
    EvChargeMetricComplete commonMetricMessage =
        genCommonMetricMessage(evChargeEvent, processTime, publishTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }
}
