package com.googlecodesamples.cloud.jss.common.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static com.google.common.truth.Truth.assertThat;

public class PubSubUtilTest {

  private static final Integer LOOP_COUNT = 200;

  private static final Logger log = LoggerFactory.getLogger(PubSubUtilTest.class);

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

  private void testRandomIntRange(int min, int max) {
    log.info("Test random integer generation...min: {}, max: {}, loop count: {}", min, max, LOOP_COUNT);
    for (int i = 0; i < LOOP_COUNT; i++) {
      Integer result = PubSubUtil.genRandomInt(min, max);
      assertThat(result).isAtLeast(min);
      assertThat(result).isAtMost(max);
    }
    log.info("Passed");
  }

  private void testRandomFloatRange(float min, float max) {
    log.info("Test random float generation...min: {}, max: {}, loop count: {}", min, max, LOOP_COUNT);
    for (int i = 0; i < LOOP_COUNT; i++) {
      Float result = PubSubUtil.genRandomFloat(min, max);
      assertThat(result).isAtLeast(min);
      assertThat(result).isAtMost(max);
    }
    log.info("Passed");
  }
}
