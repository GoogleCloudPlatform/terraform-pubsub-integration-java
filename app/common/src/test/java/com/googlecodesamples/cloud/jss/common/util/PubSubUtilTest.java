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

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Unit test for {@link PubSubUtil}. */
public class PubSubUtilTest {

  private static final Logger logger = LoggerFactory.getLogger(PubSubUtilTest.class);

  private static final Integer LOOP_COUNT = 200;

  private static final Integer TEST_RATIO_LOOP_COUNT = 10000;

  private static final Float EXPECTED_MIN_RATIO = 0.994f;

  @Test
  public void testGenRandomInt() {
    testRandomIntRange(0, 100);
    testRandomIntRange(5, 10);
  }

  @Test
  public void testGenRandomFloat() {
    testRandomFloatRange(0f, 100f);
    testRandomFloatRange(5.5f, 9.5f);
  }

  @Test
  public void testFormatFloat() {
    assertThat(PubSubUtil.formatFloat(-0.158f)).isEqualTo(-0.16f);
    assertThat(PubSubUtil.formatFloat(0f)).isEqualTo(0f);
    assertThat(PubSubUtil.formatFloat(0.512f)).isEqualTo(0.51f);
    assertThat(PubSubUtil.formatFloat(0.516f)).isEqualTo(0.52f);
    assertThat(PubSubUtil.formatFloat(0.987f)).isEqualTo(0.99f);
    assertThat(PubSubUtil.formatFloat(0.997f)).isEqualTo(1f);
  }

  @Test
  public void testGetDiffTimeInHour() {
    // 2023-05-02T00:00:00Z
    Instant start = Instant.ofEpochSecond(1682985600L);
    // 2023-05-02T00:06:00Z
    Instant end = Instant.ofEpochSecond(1682985960L);
    // expected time difference in hours: 6 minutes, or 0.1 hour
    assertThat(PubSubUtil.getDiffTimeInHour(end, start)).isEqualTo(0.1f);

    // 2023-05-02T01:30:00Z
    end = Instant.ofEpochSecond(1682991000L);
    // expected time difference in hours: 90 minutes, or 1.5 hours
    assertThat(PubSubUtil.getDiffTimeInHour(end, start)).isEqualTo(1.5f);
  }

  @Test
  public void testGetMessageData() {
    String content = "test";
    PubsubMessage message =
        PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(content)).build();
    assertThat(PubSubUtil.getMessageData(message)).isNotEmpty();
    assertThat(PubSubUtil.getMessageData(message)).isEqualTo(content);
  }

  @Test
  public void testGenProcessTimeRatio() {
    int times = 0;
    for (int i = 0; i < TEST_RATIO_LOOP_COUNT; i++) {
      float processTime = PubSubUtil.genProcessTime();
      if (0.1 <= processTime && 0.3 >= processTime) {
        times++;
      }
    }
    assertThat(((float) times / TEST_RATIO_LOOP_COUNT)).isGreaterThan(EXPECTED_MIN_RATIO);
  }

  @Test
  public void testGenProcessTimeValue() {
    Set<Float> processTimeList = new HashSet<>();
    for (int i = 0; i < LOOP_COUNT; i++) {
      float processTime = PubSubUtil.genProcessTime();
      processTimeList.add(processTime);
      assertThat(processTime).isAtLeast(0.1f);
      assertThat(processTime).isAtMost(5);
    }
    assertThat(processTimeList.size()).isGreaterThan(1);
  }

  private static void testRandomIntRange(int min, int max) {
    logger.info(
        "test random integer generation...min: {}, max: {}, loop count: {}", min, max, LOOP_COUNT);
    for (int i = 0; i < LOOP_COUNT; i++) {
      Integer result = PubSubUtil.genRandomInt(min, max);
      assertThat(result).isAtLeast(min);
      assertThat(result).isAtMost(max);
    }
    logger.info("passed");
  }

  private static void testRandomFloatRange(float min, float max) {
    logger.info(
        "test random float generation...min: {}, max: {}, loop count: {}", min, max, LOOP_COUNT);
    for (int i = 0; i < LOOP_COUNT; i++) {
      Float result = PubSubUtil.genRandomFloat(min, max);
      assertThat(result).isAtLeast(min);
      assertThat(result).isAtMost(max);
    }
    logger.info("passed");
  }
}
