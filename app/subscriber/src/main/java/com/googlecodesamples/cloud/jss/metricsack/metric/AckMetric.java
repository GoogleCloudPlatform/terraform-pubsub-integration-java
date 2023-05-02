package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsAck;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsComplete;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AckMetric extends BaseMetric<MetricsAck> {
  private static final Logger log = LoggerFactory.getLogger(AckMetric.class);

  protected AckMetric(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public MetricsAck genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    MetricsAck metricMessage = new MetricsAck();
    MetricsComplete commonMetricMessage = genCommonMetricMessage(event, processTime, publishTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }

  @Override
  public Schema getSchema() {
    return MetricsAck.getClassSchema();
  }
}
