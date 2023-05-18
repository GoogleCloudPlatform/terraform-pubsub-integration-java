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
package com.googlecodesamples.cloud.jss.common.util;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/** Unit test for {@link MessageUtil}. */
public class MessageUtilTest {

  private static final Logger logger = LoggerFactory.getLogger(MessageUtilTest.class);

  private static final List<Float> EXPECTED_CHARGE_RATE =
      Arrays.asList(20.01f, 71.99f, 100.01f, 119.99f, 250.01f);

  private static final List<Float> EXPECTED_BATTERY_CAPACITY =
      Arrays.asList(40f, 50f, 58f, 62f, 75f, 77f, 82f, 100f, 129f, 131f);

  private static final Integer EXPECTED_MIN_STATION_ID = 0;

  private static final Integer EXPECTED_MAX_STATION_ID = 100;

  private static final Integer EXPECTED_MIN_SESSION_MINUTES = 5;

  private static final Integer EXPECTED_MAX_SESSION_MINUTES = 90;

  private static final Float EXPECTED_MIN_BATTERY_PERCENTAGE = 0.05f;

  private static final Float EXPECTED_MAX_BATTERY_PERCENTAGE = 0.8f;

  private static final String ENV_GCP_LOCATION = System.getenv(PubSubConst.GOOGLE_CLOUD_LOCATION);

  private static final Integer LOOP_COUNT = 200;

  @Test
  public void testGenStationId() {
    for (int i = 0; i < LOOP_COUNT; i++) {
      Integer result = MessageUtil.genStationId();
      assertThat(result).isAtLeast(EXPECTED_MIN_STATION_ID);
      assertThat(result).isAtMost(EXPECTED_MAX_STATION_ID);
    }
  }

  @Test
  public void testGenSessionStartTime() {
    long endTime = Instant.now().getEpochSecond();
    long expected_min_session_secs = EXPECTED_MIN_SESSION_MINUTES * 60L;
    long expected_max_session_secs = EXPECTED_MAX_SESSION_MINUTES * 60L;

    for (int i = 0; i < LOOP_COUNT; i++) {
      long startTime = MessageUtil.genSessionStartTime(endTime);
      long diff = endTime - startTime;
      assertThat(diff).isAtLeast(expected_min_session_secs);
      assertThat(diff).isAtMost(expected_max_session_secs);
    }
  }

  @Test
  public void testGenAvChargeRateKw() {
    for (int i = 0; i < LOOP_COUNT; i++) {
      float chargeRateKw = MessageUtil.genAvChargeRateKw();
      assertThat(chargeRateKw).isIn(EXPECTED_CHARGE_RATE);
    }
  }

  @Test
  public void testGenBatteryCapacityKwh() {
    for (int i = 0; i < LOOP_COUNT; i++) {
      float capacityKwh = MessageUtil.genBatteryCapacityKwh();
      assertThat(capacityKwh).isIn(EXPECTED_BATTERY_CAPACITY);
    }
  }

  @Test
  public void testGenBatteryLevelStart() {
    for (int i = 0; i < LOOP_COUNT; i++) {
      float percentage = MessageUtil.genBatteryLevelStart();
      assertThat(percentage).isAtLeast(EXPECTED_MIN_BATTERY_PERCENTAGE);
      assertThat(percentage).isAtMost(EXPECTED_MAX_BATTERY_PERCENTAGE);
    }
  }

  @Test
  public void testGenRandomEvent() throws UnknownHostException {
    assumeTrue(LogMessage.WARN_GCP_PROJECT_NOT_SET, StringUtils.hasText(ENV_GCP_LOCATION));

    for (int i = 0; i < LOOP_COUNT; i++) {
      Event event = MessageUtil.genRandomEvent();
      assertThat(event).isNotNull();
      assertThat(event.getStationId()).isAtLeast(EXPECTED_MIN_STATION_ID);
      assertThat(event.getStationId()).isAtMost(EXPECTED_MAX_STATION_ID);
      assertThat(event.getAvgChargeRateKw()).isIn(EXPECTED_CHARGE_RATE);
      assertThat(event.getBatteryCapacityKwh()).isIn(EXPECTED_BATTERY_CAPACITY);
      assertThat(event.getBatteryLevelStart()).isAtLeast(EXPECTED_MIN_BATTERY_PERCENTAGE);
      assertThat(event.getBatteryLevelStart()).isAtMost(EXPECTED_MAX_BATTERY_PERCENTAGE);
    }
  }

  @Test
  public void testConvertToPubSubMessage() throws IOException {
    assumeTrue(LogMessage.WARN_GCP_PROJECT_NOT_SET, StringUtils.hasText(ENV_GCP_LOCATION));

    for (int i = 0; i < LOOP_COUNT; i++) {
      Event event = MessageUtil.genRandomEvent();
      PubsubMessage message = MessageUtil.convertToPubSubMessage(event, Event.getClassSchema());
      assertThat(message).isNotNull();

      String eventJson = event.toString().replaceAll("\\s", "");
      String messageJson = message.getData().toStringUtf8();

      JsonObject eventJsonObj = JsonParser.parseString(eventJson).getAsJsonObject();
      JsonObject messageJsonObj = JsonParser.parseString(messageJson).getAsJsonObject();
      logger.info("messageJsonObj: {}", messageJsonObj);

      assertThat(messageJsonObj.entrySet()).containsAnyIn(eventJsonObj.entrySet());
      assertThat(messageJsonObj.get("station_id").getAsInt()).isAtLeast(EXPECTED_MIN_STATION_ID);
      assertThat(messageJsonObj.get("station_id").getAsInt()).isAtMost(EXPECTED_MAX_STATION_ID);
      assertThat(messageJsonObj.get("location").getAsString()).isEqualTo(ENV_GCP_LOCATION);
      assertThat(messageJsonObj.get("avg_charge_rate_kw").getAsFloat()).isIn(EXPECTED_CHARGE_RATE);
      assertThat(messageJsonObj.get("battery_capacity_kwh").getAsFloat())
          .isIn(EXPECTED_BATTERY_CAPACITY);
      assertThat(messageJsonObj.get("battery_level_start").getAsFloat())
          .isAtLeast(EXPECTED_MIN_BATTERY_PERCENTAGE);
      assertThat(messageJsonObj.get("battery_level_start").getAsFloat())
          .isAtMost(EXPECTED_MAX_BATTERY_PERCENTAGE);
    }
  }

  @Test
  public void testConvertToAvroEvent() throws IOException {
    String content =
        "{\"session_id\":\"00c180a3-afbb-4766-a6ed-086a983e353d\","
            + "\"station_id\":74,\"location\":\"us-central1\","
            + "\"session_start_time\":1683622175000000,\"session_end_time\":1683625295000000,"
            + "\"avg_charge_rate_kw\":100.01,\"battery_capacity_kwh\":62.0,\"battery_level_start\":0.75,\"event_node\":\"10.0.0.1\"}";

    PubsubMessage message =
        PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(content)).build();
    Event event = MessageUtil.convertToAvroEvent(message);
    logger.info("event: {}", event);
    assertThat(event).isNotNull();
    assertThat(event.getStationId()).isAtLeast(EXPECTED_MIN_STATION_ID);
    assertThat(event.getStationId()).isAtMost(EXPECTED_MAX_STATION_ID);
    assertThat(event.getLocation().toString()).isEqualTo("us-central1");
    assertThat(event.getAvgChargeRateKw()).isIn(EXPECTED_CHARGE_RATE);
    assertThat(event.getBatteryCapacityKwh()).isIn(EXPECTED_BATTERY_CAPACITY);
    assertThat(event.getBatteryLevelStart()).isAtLeast(EXPECTED_MIN_BATTERY_PERCENTAGE);
    assertThat(event.getBatteryLevelStart()).isAtMost(EXPECTED_MAX_BATTERY_PERCENTAGE);
  }
}
