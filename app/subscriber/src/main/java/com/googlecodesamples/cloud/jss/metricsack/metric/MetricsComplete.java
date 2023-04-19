package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.googlecodesamples.cloud.jss.metricsack.util.SubscribeUtil;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import org.springframework.stereotype.Component;

@Component
public class MetricsComplete extends Metric<EvChargeMetricComplete> {
  protected MetricsComplete(PubSubTemplate pubSubTemplate) {
    super(pubSubTemplate);
  }

  @Override
  public EvChargeMetricComplete genMetricMessage(EvChargeEvent evChargeEvent, float processTime) {
    EvChargeMetricComplete metricMessage = genCommonMetricMessage(evChargeEvent, processTime);
    genExtraFields(metricMessage);
    return metricMessage;
  }

  public void genExtraFields(EvChargeMetricComplete evChargeMetric) {
    float batteryLevelStart = evChargeMetric.getBatteryLevelStart();
    float batteryCapacityKwh = evChargeMetric.getBatteryCapacityKwh();
    float batteryLevel =
        batteryLevelStart
            + evChargeMetric.getAvgChargeRateKw()
                * evChargeMetric.getSessionDurationHr()
                / batteryCapacityKwh;
    float batteryLevelEnd = Math.min(1.0f, SubscribeUtil.formatFloat(batteryLevel));
    evChargeMetric.setBatteryLevelEnd(batteryLevelEnd);
    float chargedTotalKwh = (batteryLevelEnd - batteryLevelStart) * batteryCapacityKwh;
    evChargeMetric.setChargedTotalKwh(SubscribeUtil.formatFloat(chargedTotalKwh));
  }
}
