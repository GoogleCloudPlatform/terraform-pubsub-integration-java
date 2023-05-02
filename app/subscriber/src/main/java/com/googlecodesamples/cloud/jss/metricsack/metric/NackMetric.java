package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsNack;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class NackMetric extends BaseMetric<MetricsNack> {
  private static final Logger log = LoggerFactory.getLogger(NackMetric.class);

  protected NackMetric(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("ConsumerAckOrNack nack");
    consumer.nack(); // nack every message receives 🐞
  }

  @Override
  public MetricsNack genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    MetricsNack metricMessage = new MetricsNack();
    MetricsComplete commonMetricMessage = genCommonMetricMessage(event, processTime, publishTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }

  @Override
  public Schema getSchema() {
    return MetricsNack.getClassSchema();
  }
}
