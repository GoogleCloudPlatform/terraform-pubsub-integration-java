package com.cienet.sub.metric;

import com.cienet.sub.service.PublishService;
import com.cienet.sub.util.PublishUtil;
import com.cienet.sub.utilities.EvChargeEvent;
import com.cienet.sub.utilities.EvChargeMetricAck;
import com.cienet.sub.utilities.EvChargeMetricComplete;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class MetricsAck extends Metric<EvChargeMetricAck> {
  private static final Logger log = LoggerFactory.getLogger(MetricsAck.class);

  protected MetricsAck(PublishService publishService) {
    super(publishService);
  }

  @Override
  public ByteString convertMessage(EvChargeMetricAck message) {
    return PublishUtil.jsonEncode(message, EvChargeMetricAck.getClassSchema());
  }

  @Override
  public EvChargeMetricAck genMetricData(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricAck evChargeMetricAck = new EvChargeMetricAck();
    EvChargeMetricComplete evChargeMetricComplete = genCommonData(evChargeEvent, processTime);
    BeanUtils.copyProperties(evChargeMetricComplete, evChargeMetricAck);
    return evChargeMetricAck;
  }
}
