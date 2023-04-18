package com.cienet.sub.metric;

import com.cienet.sub.service.PublishService;
import com.cienet.sub.util.PublishUtil;
import com.cienet.sub.utilities.EvChargeEvent;
import com.cienet.sub.utilities.EvChargeMetricAck;
import com.cienet.sub.utilities.EvChargeMetricComplete;
import com.cienet.sub.utilities.EvChargeMetricNack;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsNack extends Metric<EvChargeMetricNack> {
  private static final Logger log = LoggerFactory.getLogger(MetricsNack.class);

  protected MetricsNack(PublishService publishService) {
    super(publishService);
  }

  @Override
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    log.info("consumer nack");
    consumer.nack(); // nack every message receives 🐞
  }

  @Override
  public ByteString convertMessage(EvChargeMetricNack message) {
    return PublishUtil.jsonEncode(message, EvChargeMetricNack.getClassSchema());
  }

  @Override
  public EvChargeMetricNack genMetricData(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricNack evChargeMetricNack = new EvChargeMetricNack();
    EvChargeMetricComplete evChargeMetricComplete = genCommonData(evChargeEvent, processTime);
    BeanUtils.copyProperties(evChargeMetricComplete, evChargeMetricNack);
    return evChargeMetricNack;
  }
}
