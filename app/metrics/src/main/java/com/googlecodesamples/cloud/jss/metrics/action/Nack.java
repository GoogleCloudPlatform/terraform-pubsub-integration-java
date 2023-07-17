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
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.generated.MetricsNack;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** MetricNack specified actions. */
@Component
@ConditionalOnProperty(name = "metric.app.type", havingValue = PubSubConst.METRICS_NACK)
public class Nack extends BaseAction<MetricsNack> {

  private static final Logger logger = LoggerFactory.getLogger(Nack.class);

  public Nack(MetricPublisherService service) {
    setService(service);
  }

  @Override
  public Schema getSchema() {
    return MetricsNack.getClassSchema();
  }

  @Override
  public MetricsNack respond(
      AckReplyConsumer consumer, PubsubMessage message, float processTime, Timestamp publishTime) {
    logger.info("consumer response: NACK");
    consumer.nack(); // Simulate a bug 🐞 and nack the message.
    return null;
  }

  @Override
  public void postProcess(MetricsNack newMessage) {
    logger.info("nack messages not publishing to metric topic");
  }
}
