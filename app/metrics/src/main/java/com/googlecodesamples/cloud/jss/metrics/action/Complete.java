/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecodesamples.cloud.jss.metrics.action;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** MetricComplete specified actions. */
@Component
public class Complete extends BaseAction<MetricsComplete> {

  private static final Logger logger = LoggerFactory.getLogger(Complete.class);

  public Complete(MetricPublisherService publishService) {
    super(publishService);
  }

  @Override
  public MetricsComplete genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    logger.info(
        "generate complete metric message, event: {}, processTime {}, publishTime {}",
        event,
        processTime,
        publishTime);

    MetricsComplete message = genCommonMetricMessage(event, processTime, publishTime);
    message.setBatteryLevelEnd(genBatteryLevelEnd(message));
    message.setChargedTotalKwh(genChargedTotalKwh(message));
    return message;
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
