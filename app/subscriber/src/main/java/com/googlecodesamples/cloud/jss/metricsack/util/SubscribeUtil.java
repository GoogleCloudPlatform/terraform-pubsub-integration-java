package com.googlecodesamples.cloud.jss.metricsack.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscribeUtil {
  private static final Logger log = LoggerFactory.getLogger(SubscribeUtil.class);
  private static final String UTC_TIME = "UTC";
  private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final Random RANDOM = new Random();
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

  public static float genRandomFloat(float min, float max) {
    return RANDOM.nextFloat() * (max - min) + min;
  }

  public static synchronized String formatTime(long currentTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(UTC_TIME));
    return DATE_FORMAT.format(currentTime);
  }

  public static synchronized long parseTime(String time) {
    try {
      DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(UTC_TIME));
      return DATE_FORMAT.parse(time).getTime();
    } catch (ParseException e) {
      log.error("ParseTime ParseException", e);
    }
    return -1;
  }

  public static float getDiffTimeInHour(String endTime, String startTime) {
    return formatFloat((parseTime(endTime) - parseTime(startTime)) / (1000f * 60 * 60));
  }

  public static float formatFloat(float floatValue) {
    BigDecimal value = new BigDecimal(floatValue);
    value = value.setScale(2, RoundingMode.HALF_UP);
    return value.floatValue();
  }
}
