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

package com.googlecodesamples.cloud.jss.metrics.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Backend service controller to asynchronously publish messages to GCP BigQuery. */
@Service
public class MetricPublisherService extends BasePublisherService {

  private static final Logger logger = LoggerFactory.getLogger(MetricPublisherService.class);

  public MetricPublisherService(Publisher publisher) {
    super(publisher);
  }

  @Override
  public void init() {
    logger.info("initializing MetricPublisherService");
  }

  @Override
  public void shutdown() {
    logger.info("shutting down MetricPublisherService");
  }
}
