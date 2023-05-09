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
package com.googlecodesamples.cloud.jss.common.action;

import com.google.protobuf.Timestamp;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class BaseActionTest {
  private static final String SESSION_ID = UUID.randomUUID().toString();
  private static final int STATION_ID = 1;
  private static final String LOCATION = "us-west1";
  private static final Instant SESSION_END_TIME = Instant.now();
  private static final Instant SESSION_START_TIME = SESSION_END_TIME.minusSeconds(10);
  private static final float AVG_CHARGE_RATE_KW = 20.2f;
  private static final float BATTERY_CAPACITY_KWH = 40;
  private static final float BATTERY_LEVEL_START = 0.05f;
  private static final float PROCESS_TIME = 0.1f;
  private static final Timestamp PUBLISH_TIME =
      Timestamp.newBuilder().setSeconds(SESSION_END_TIME.getEpochSecond()).build();

  @Test
  public void testGenCommonMetricMessage() {
    BaseAction baseAction = Mockito.mock(BaseAction.class, Answers.CALLS_REAL_METHODS);
    MetricsComplete metricMessage =
        baseAction.genCommonMetricMessage(genEvent(), PROCESS_TIME, PUBLISH_TIME);
    assertThat(metricMessage).isNotNull();
    assertThat(metricMessage.getSessionId()).isEqualTo(SESSION_ID);
    assertThat(metricMessage.getProcessingTimeSec()).isEqualTo(PROCESS_TIME);
    assertThat(metricMessage.getPublishTimestamp().getEpochSecond())
        .isEqualTo(PUBLISH_TIME.getSeconds());
  }

  private Event genEvent() {
    return Event.newBuilder()
        .setSessionId(SESSION_ID)
        .setStationId(STATION_ID)
        .setLocation(LOCATION)
        .setSessionStartTime(SESSION_START_TIME)
        .setSessionEndTime(SESSION_END_TIME)
        .setAvgChargeRateKw(AVG_CHARGE_RATE_KW)
        .setBatteryCapacityKwh(BATTERY_CAPACITY_KWH)
        .setBatteryLevelStart(BATTERY_LEVEL_START)
        .build();
  }
}
