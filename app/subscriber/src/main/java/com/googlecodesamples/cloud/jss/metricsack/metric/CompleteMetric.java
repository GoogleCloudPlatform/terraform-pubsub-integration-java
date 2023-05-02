package com.googlecodesamples.cloud.jss.metricsack.metric;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import com.googlecodesamples.cloud.jss.common.utilities.MetricsComplete;
import com.googlecodesamples.cloud.jss.metricsack.service.MessageService;
import com.googlecodesamples.cloud.jss.metricsack.service.PublishService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CompleteMetric extends BaseMetric<MetricsComplete> {
  private static final Logger log = LoggerFactory.getLogger(CompleteMetric.class);

  protected CompleteMetric(PublishService publishService, MessageService messageService) {
    super(publishService, messageService);
  }

  @Override
  public MetricsComplete genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    MetricsComplete metricMessage = genCommonMetricMessage(event, processTime, publishTime);
    metricMessage.setBatteryLevelEnd(genBatteryLevelEnd(metricMessage));
    metricMessage.setChargedTotalKwh(genChargedTotalKwh(metricMessage));
    return metricMessage;
  }

  @Override
  public Schema getSchema() {
    return MetricsComplete.getClassSchema();
  }

  private float genBatteryLevelEnd(MetricsComplete metric) {
    float batteryLevelEnd =
        PubSubUtil.formatFloat(
            metric.getBatteryLevelStart()
                + (metric.getAvgChargeRateKw()
                    * metric.getSessionDurationHr()
                    / metric.getBatteryCapacityKwh()));
    return Math.min(1.0f, batteryLevelEnd);
  }

  private float genChargedTotalKwh(MetricsComplete metric) {
    return PubSubUtil.formatFloat(
        ((metric.getBatteryLevelEnd() - metric.getBatteryLevelStart())
            * metric.getBatteryCapacityKwh()));
  }
}
