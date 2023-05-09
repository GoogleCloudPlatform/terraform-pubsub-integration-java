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

import com.google.pubsub.v1.PubsubMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class PubSubUtil {

  /**
   * Generate random integer value.
   *
   * @param min minimum generated value
   * @param max maximum generated value
   * @return random integer
   */
  public static int genRandomInt(int min, int max) {
    return ThreadLocalRandom.current().nextInt((max - min + 1)) + min;
  }

  /**
   * Generate random float value.
   *
   * @param min minimum generated value
   * @param max maximum generated value
   * @return random float
   */
  public static float genRandomFloat(float min, float max) {
    return formatFloat(ThreadLocalRandom.current().nextFloat() * (max - min) + min);
  }

  /**
   * Format float value
   *
   * @param floatValue value to be formatted
   * @return formatted value
   */
  public static float formatFloat(float floatValue) {
    BigDecimal value = new BigDecimal(floatValue);
    value = value.setScale(2, RoundingMode.HALF_UP);
    return value.floatValue();
  }

  /**
   * Calculate the difference of hours between startTime and endTime
   *
   * @param endTime   end of time
   * @param startTime start of time
   * @return difference in hours
   */
  public static float getDiffTimeInHour(Instant endTime, Instant startTime) {
    return formatFloat((endTime.getEpochSecond() - startTime.getEpochSecond()) / (60f * 60));
  }

  public static String getMessageData(PubsubMessage message) {
    return message.getData().toStringUtf8();
  }
}
