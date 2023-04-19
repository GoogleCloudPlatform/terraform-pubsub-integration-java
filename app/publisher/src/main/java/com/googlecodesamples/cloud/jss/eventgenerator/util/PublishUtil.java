package com.googlecodesamples.cloud.jss.eventgenerator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishUtil {
  private static final Logger log = LoggerFactory.getLogger(PublishUtil.class);
  private static final Random RANDOM = new Random();
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_INSTANT;

  public static String formatTime(long second) {
    return DATE_FORMAT.format(Instant.ofEpochSecond(second));
  }

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
}
