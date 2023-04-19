package com.googlecodesamples.cloud.jss.metricsack.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscribeUtil {
  private static final Logger log = LoggerFactory.getLogger(SubscribeUtil.class);
  private static final Random RANDOM = new Random();
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;

  public static float genRandomFloat(float min, float max) {
    return RANDOM.nextFloat() * (max - min) + min;
  }

  public static String formatTime(long second) {
    return DATE_FORMAT.format(Instant.ofEpochSecond(second));
  }

  public static float getDiffTimeInHour(String endTime, String startTime) {
    return formatFloat((parseTime(endTime) - parseTime(startTime)) / (60f * 60));
  }

  public static float formatFloat(float floatValue) {
    BigDecimal value = new BigDecimal(floatValue);
    value = value.setScale(2, RoundingMode.HALF_UP);
    return value.floatValue();
  }

  private static long parseTime(String time) {
    return Instant.from(DATE_FORMAT.parse(time)).getEpochSecond();
  }
}
