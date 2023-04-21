package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import com.googlecodesamples.cloud.jss.metricsack.util.SubscribeUtil;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MetricsComplete extends Metric<EvChargeMetricComplete> {
  private static final Logger log = LoggerFactory.getLogger(MetricsComplete.class);

  protected MetricsComplete(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public EvChargeMetricComplete genMetricMessage(
      EvChargeEvent evChargeEvent, float processTime, Timestamp publishTime) {
    EvChargeMetricComplete metricMessage =
        genCommonMetricMessage(evChargeEvent, processTime, publishTime);
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
    float batteryLevelEnd = Math.min(1.0f, batteryLevel);
    evChargeMetric.setBatteryLevelEnd(SubscribeUtil.formatFloat(batteryLevelEnd));
    float chargedTotalKwh =
        SubscribeUtil.formatFloat(((batteryLevelEnd - batteryLevelStart) * batteryCapacityKwh));
    evChargeMetric.setChargedTotalKwh(chargedTotalKwh);
  }
}
