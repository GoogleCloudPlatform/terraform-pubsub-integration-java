package com.cienet.sub.metric;

import com.cienet.sub.utilities.EvChargeMetric;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

@Component
public class MetricsComplete extends Metric {
  protected MetricsComplete(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }

  @Override
  public void genExtraFields(EvChargeMetric evChargeMetric) {
    float batteryLevelStart = evChargeMetric.getBatteryLevelStart();
    float batteryCapacityKwh = evChargeMetric.getBatteryCapacityKwh();
    float batteryLevel =
        batteryLevelStart
            + evChargeMetric.getAvgChargeRateKw()
                * evChargeMetric.getSessionDurationHr()
                / batteryCapacityKwh;
    float batteryLevelEnd = Math.min(1.0f, batteryLevel);
    evChargeMetric.setBatteryLevelEnd(batteryLevelEnd);
    float chargedTotalKwh = (batteryLevelEnd - batteryLevelStart) * batteryCapacityKwh;
    evChargeMetric.setChargedTotalKwh(chargedTotalKwh);
  }
}
