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
import com.googlecodesamples.cloud.jss.common.generated.MetricsAck;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/** MetricAck specified actions. */
@Component
public class Ack extends BaseAction<MetricsAck> {

  private static final Logger logger = LoggerFactory.getLogger(Ack.class);

  public Ack(MetricPublisherService publishService) {
    super(publishService);
  }

  @Override
  public MetricsAck genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    logger.info(
        "generate ack metric message, event: {}, processTime {}, publishTime {}",
        event,
        processTime,
        publishTime);

    MetricsAck message = new MetricsAck();
    MetricsComplete commonMessage = genCommonMetricMessage(event, processTime, publishTime);
    BeanUtils.copyProperties(commonMessage, message);
    return message;
  }

  @Override
  public Schema getSchema() {
    return MetricsAck.getClassSchema();
  }
}
