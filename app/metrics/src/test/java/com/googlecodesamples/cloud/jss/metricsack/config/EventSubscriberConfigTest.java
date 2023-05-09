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

import com.googlecodesamples.cloud.jss.metrics.config.EventSubscriberConfig;
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
@EnableConfigurationProperties(value = EventSubscriberConfig.class)
@TestPropertySource("classpath:application-test.properties")
public class EventSubscriberConfigTest {

  private static final Logger logger = LoggerFactory.getLogger(EventSubscriberConfigTest.class);

  private static final String EXPECTED_SUBSCRIPTION = "test-subscription";

  private static final Integer EXPECTED_PARALLEL_PULL = 5;

  private static final Integer EXPECTED_EXECUTOR_THREADS = 4;

  private static final Long EXPECTED_OUTSTANDING_MSG = 100L;

  private static final List<Integer> NEGATIVE_INPUTS = Arrays.asList(0, -1, -10);

  private static final String ERROR_MSG_NEGATIVE_INPUT = "should be greater than zero";

  private static final String ERROR_MSG_EMPTY_INPUT = "should not be empty";

  @Autowired
  private EventSubscriberConfig config;

  @Test
  public void testDefaultPropertyBindings() {
    assertThat(config).isNotNull();
    assertThat(config.getEventSubscription()).isEqualTo(EXPECTED_SUBSCRIPTION);
    assertThat(config.getParallelPull()).isEqualTo(EXPECTED_PARALLEL_PULL);
    assertThat(config.getExecutorThreads()).isEqualTo(EXPECTED_EXECUTOR_THREADS);
    assertThat(config.getOutstandingMessages()).isEqualTo(EXPECTED_OUTSTANDING_MSG);
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

//  @Test
//  public void testUnexpectedParallelPull() {
//    for (Integer input : NEGATIVE_INPUTS) {
//      try {
//        config.setParallelPull(input);
//      } catch (IllegalArgumentException e) {
//        assertThat(e).isInstanceOf(IllegalArgumentException.class);
//        assertThat(e).hasMessageThat().contains(ERROR_MSG_NEGATIVE_INPUT);
//      }
//    }
//  }

  @Test
  public void testNullSubscription() {
    try {
      config.setEventSubscription(null);
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }

  @Test
  public void testEmptySubscription() {
    try {
      config.setEventSubscription("");
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }
}
