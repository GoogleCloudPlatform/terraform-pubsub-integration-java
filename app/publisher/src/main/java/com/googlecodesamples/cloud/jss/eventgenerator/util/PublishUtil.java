package com.googlecodesamples.cloud.jss.eventgenerator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishUtil {
  private static final Logger log = LoggerFactory.getLogger(PublishUtil.class);
  private static final String UTC_TIME = "UTC";
  private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final Random RANDOM = new Random();
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

  public static synchronized String formatTime(long currentTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(UTC_TIME));
    return DATE_FORMAT.format(currentTime);
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
