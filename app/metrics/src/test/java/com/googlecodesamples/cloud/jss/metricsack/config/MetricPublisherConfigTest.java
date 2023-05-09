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
package com.googlecodesamples.cloud.jss.metricsack.config;

import com.googlecodesamples.cloud.jss.metrics.config.MetricPublisherConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableConfigurationProperties(value = MetricPublisherConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class MetricPublisherConfigTest {

  private static final Logger logger = LoggerFactory.getLogger(MetricPublisherConfigTest.class);

  private static final String EXPECTED_TOPIC_NAME = "test-metric-topic";

  private static final Integer EXPECTED_EXECUTOR_THREADS = 5;

  private static final Long EXPECTED_OUTSTANDING_MSG = 150L;

  private static final List<Integer> NEGATIVE_INPUTS = Arrays.asList(0, -1, -10);

  private static final String ERROR_MSG_NEGATIVE_INPUT = "should be greater than zero";

  private static final String ERROR_MSG_EMPTY_INPUT = "should not be empty";

  @Autowired
  private MetricPublisherConfig config;

  @Test
  public void testDefaultPropertyBindings() {
    assertThat(config).isNotNull();
    assertThat(config.getTopicName()).isEqualTo(EXPECTED_TOPIC_NAME);
    assertThat(config.getExecutorThreads()).isEqualTo(EXPECTED_EXECUTOR_THREADS);
    assertThat(config.getOutstandingMessages()).isNull();
    logger.info("config: {}", config.getInfo());
  }

  @Test
  public void testUnexpectedExecutorThreadNumber() {
    for (Integer input : NEGATIVE_INPUTS) {
      try {
        config.setExecutorThreads(input);
      } catch (IllegalArgumentException e) {
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
        assertThat(e).hasMessageThat().contains(ERROR_MSG_NEGATIVE_INPUT);
      }
    }
  }

  @Test
  public void testUnexpectedBatchSize() {
    for (Integer input : NEGATIVE_INPUTS) {
      try {
        config.setBatchSize(input.longValue());
      } catch (IllegalArgumentException e) {
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
        assertThat(e).hasMessageThat().contains(ERROR_MSG_NEGATIVE_INPUT);
      }
    }
  }

  @Test
  public void testNullTopicName() {
    try {
      config.setTopicName(null);
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }

  @Test
  public void testEmptyTopicName() {
    try {
      config.setTopicName("");
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }
}
