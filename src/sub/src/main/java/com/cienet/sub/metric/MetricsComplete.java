package com.cienet.sub.metric;

import com.cienet.sub.service.PublishService;
import com.cienet.sub.util.PublishUtil;
import com.cienet.sub.utilities.EvChargeEvent;
import com.cienet.sub.utilities.EvChargeMetricComplete;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MetricsComplete extends Metric<EvChargeMetricComplete> {
  private static final Logger log = LoggerFactory.getLogger(MetricsComplete.class);

  protected MetricsComplete(PublishService publishService) {
    super(publishService);
  }

  @Override
  public ByteString convertMessage(EvChargeMetricComplete message) {
    return PublishUtil.jsonEncode(message, EvChargeMetricComplete.getClassSchema());
  }

  @Override
  public EvChargeMetricComplete genMetricData(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricComplete evChargeMetricComplete = genCommonData(evChargeEvent, processTime);
    return genExtraFields(evChargeMetricComplete);
  }

  public EvChargeMetricComplete genExtraFields(EvChargeMetricComplete evChargeMetricComplete) {
    float batteryLevelStart = evChargeMetricComplete.getBatteryLevelStart();
    float batteryCapacityKwh = evChargeMetricComplete.getBatteryCapacityKwh();
    float batteryLevel =
        batteryLevelStart
            + evChargeMetricComplete.getAvgChargeRateKw()
                * evChargeMetricComplete.getSessionDurationHr()
                / batteryCapacityKwh;
    float batteryLevelEnd = Math.min(1.0f, batteryLevel);
    evChargeMetricComplete.setBatteryLevelEnd(batteryLevelEnd);
    float chargedTotalKwh = (batteryLevelEnd - batteryLevelStart) * batteryCapacityKwh;
    evChargeMetricComplete.setChargedTotalKwh(chargedTotalKwh);
    return evChargeMetricComplete;
  }
}
