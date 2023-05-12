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

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.generated.MetricsNack;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** MetricNack specified actions. */
@Component
public class Nack extends BaseAction<MetricsNack> {

  private static final Logger logger = LoggerFactory.getLogger(Nack.class);

  public Nack(MetricPublisherService publishService) {
    super(publishService);
  }

  @Override
  public void consumerAckOrNack(AckReplyConsumer consumer) {
    logger.info("consumerAckOrNack: nack");
    consumer.nack(); // nack every message receives 🐞
  }

  @Override
  public MetricsNack genMetricMessage(Event event, float processTime, Timestamp publishTime) {
    logger.info("nack messages not generating metric message");
    return null;
  }

  @Override
  public Schema getSchema() {
    return MetricsNack.getClassSchema();
  }

  @Override
  public void postProcessMessage(MetricsNack newMessage) {
    logger.info("nack messages not publishing to metric topic");
  }
}
