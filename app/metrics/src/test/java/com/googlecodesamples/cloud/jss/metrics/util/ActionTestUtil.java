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
package com.googlecodesamples.cloud.jss.metrics.util;

import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.util.MessageUtil;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/** Reusable test utility methods for action tests. */
public class ActionTestUtil {

  public static final Integer SESSION_DIFF_MINUTES = 10;

  public static final String EXPECTED_SESSION_ID = UUID.randomUUID().toString();

  public static final Integer EXPECTED_STATION_ID = 1;

  public static final String EXPECTED_LOCATION = "us-west1";

  public static final Instant EXPECTED_SESSION_END_TIME =
      Instant.ofEpochSecond(Instant.now().getEpochSecond());

  public static final Float EXPECTED_AVG_CHARGE_RATE_KW = 20.2f;

  public static final Float EXPECTED_BATTERY_CAPACITY_KWH = 40f;

  public static final Float EXPECTED_BATTERY_LEVEL_START = 0.05f;

  public static final Float EXPECTED_PROCESS_TIME = 0.1f;

  public static final Integer EXPECTED_MIN_SESSION_MINUTES = 5;

  public static final Integer EXPECTED_MAX_SESSION_MINUTES = 90;

  public static final Instant EXPECTED_SESSION_START_TIME =
      EXPECTED_SESSION_END_TIME.minusSeconds(SESSION_DIFF_MINUTES * 60);

  public static final Instant EXPECTED_PUBLISH_TIME =
      Instant.ofEpochSecond(EXPECTED_SESSION_END_TIME.getEpochSecond());

  public static final Float EXPECTED_BATTERY_LEVEL_END = getBatteryLevelEnd();

  public static final Float EXPECTED_CHARGED_TOTAL_KWH = getChargedTotalKwh();

  public static final Instant EXPECTED_ACK_TIME =
      Instant.ofEpochSecond(
          EXPECTED_SESSION_END_TIME
              .plusMillis((long) (EXPECTED_PROCESS_TIME * 1000))
              .getEpochSecond());

  public static final Timestamp PUBLISH_TIME =
      Timestamp.newBuilder().setSeconds(EXPECTED_SESSION_END_TIME.getEpochSecond()).build();

  public static Event genEvent() {
    return Event.newBuilder()
        .setSessionId(EXPECTED_SESSION_ID)
        .setStationId(EXPECTED_STATION_ID)
        .setLocation(EXPECTED_LOCATION)
        .setSessionStartTime(EXPECTED_SESSION_START_TIME)
        .setSessionEndTime(EXPECTED_SESSION_END_TIME)
        .setAvgChargeRateKw(EXPECTED_AVG_CHARGE_RATE_KW)
        .setBatteryCapacityKwh(EXPECTED_BATTERY_CAPACITY_KWH)
        .setBatteryLevelStart(EXPECTED_BATTERY_LEVEL_START)
        .build();
  }

  public static PubsubMessage genEventMessage(Event event) throws IOException {
    return MessageUtil.convertToPubSubMessage(event, Event.getClassSchema());
  }

  public static float getBatteryLevelEnd() {
    return ActionUtil.genBatteryLevelEnd(
        EXPECTED_BATTERY_LEVEL_START,
        EXPECTED_AVG_CHARGE_RATE_KW,
        EXPECTED_BATTERY_CAPACITY_KWH,
        getSessionDurationHr());
  }

  public static float getChargedTotalKwh() {
    return ActionUtil.genChargedTotalKwh(
        EXPECTED_BATTERY_LEVEL_START, EXPECTED_BATTERY_LEVEL_END, EXPECTED_BATTERY_CAPACITY_KWH);
  }

  private static float getSessionDurationHr() {
    return PubSubUtil.getDiffTimeInHour(EXPECTED_SESSION_END_TIME, EXPECTED_SESSION_START_TIME);
  }
}
