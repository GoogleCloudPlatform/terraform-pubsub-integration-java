package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.common.utilities.EvChargeMetricComplete;
import com.googlecodesamples.cloud.jss.common.utilities.EvChargeMetricNack;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsNack extends Metric<EvChargeMetricNack> {
  private static final Logger log = LoggerFactory.getLogger(MetricsNack.class);

  protected MetricsNack(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("ConsumerAckOrNack nack");
    consumer.nack(); // nack every message receives 🐞
  }

  @Override
  public EvChargeMetricNack genMetricMessage(
      EvChargeEvent evChargeEvent, float processTime, Timestamp publishTime) {
    EvChargeMetricNack metricMessage = new EvChargeMetricNack();
    EvChargeMetricComplete commonMetricMessage =
        genCommonMetricMessage(evChargeEvent, processTime, publishTime);
    BeanUtils.copyProperties(commonMetricMessage, metricMessage);
    return metricMessage;
  }
}
