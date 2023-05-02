package com.googlecodesamples.cloud.jss.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubUtil {
  private static final Logger log = LoggerFactory.getLogger(PubSubUtil.class);
  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

  public static int genRandomInt(int min, int max) {
    return RANDOM.nextInt((max - min + 1)) + min;
  }

  public static float genRandomFloat(float min, float max) {
    return formatFloat(RANDOM.nextFloat() * (max - min) + min);
  }

  public static float formatFloat(float floatValue) {
    BigDecimal value = new BigDecimal(floatValue);
    value = value.setScale(2, RoundingMode.HALF_UP);
    return value.floatValue();
  }

  public static float getDiffTimeInHour(Instant endTime, Instant startTime) {
    return formatFloat((endTime.getEpochSecond() - startTime.getEpochSecond()) / (60f * 60));
  }
}
