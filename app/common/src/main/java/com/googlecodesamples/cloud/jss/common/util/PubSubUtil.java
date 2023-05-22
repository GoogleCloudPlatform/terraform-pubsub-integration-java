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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

/** Reusable utility functions. */
public class PubSubUtil {

  private static final Float DISTRIB_MIN_PROCESS_TIME = 0.1f;

  private static final Float DISTRIB_MAX_PROCESS_TIME = 0.3f;

  private static final Float MAX_PROCESS_TIME = 5f;

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
    return (ThreadLocalRandom.current().nextFloat() * (max - min) + min);
  }

  /**
   * Format float value
   *
   * @param floatValue value to be formatted
   * @return formatted value
   */
  public static float formatFloat(float floatValue) {
    BigDecimal value = new BigDecimal(floatValue);
    value = value.setScale(PubSubConst.SCALE, PubSubConst.ROUNDING_MODE);
    return value.floatValue();
  }

  /**
   * Calculate the difference of hours between startTime and endTime
   *
   * @param endTime end of time
   * @param startTime start of time
   * @return difference in hours
   */
  public static float getDiffTimeInHour(Instant endTime, Instant startTime) {
    return formatFloat((endTime.getEpochSecond() - startTime.getEpochSecond()) / (60f * 60));
  }

  public static String getMessageData(PubsubMessage message) {
    return message.getData().toStringUtf8();
  }

  /**
   * Generate random process time.
   *
   * @return random process time
   */
  public static float genProcessTime() {
    float ratio = PubSubUtil.genRandomFloat(0, 100);
    if (ratio <= DISTRIB_MIN_PROCESS_TIME) {
      return PubSubUtil.genRandomFloat(DISTRIB_MIN_PROCESS_TIME, MAX_PROCESS_TIME);
    } else {
      return PubSubUtil.genRandomFloat(DISTRIB_MIN_PROCESS_TIME, DISTRIB_MAX_PROCESS_TIME);
    }
  }

  /**
   * Get default credentials.
   *
   * @return {@link GoogleCredentials}
   * @throws IOException if the credentials file is not found
   */
  public static GoogleCredentials getGCPCredentials() throws IOException {
    return GoogleCredentials.getApplicationDefault();
  }

  /**
   * Get value of GOOGLE_CLOUD_PROJECT environment variable.
   *
   * @return value of env GOOGLE_CLOUD_PROJECT
   */
  public static String getEnvProjectId() {
    return System.getenv(PubSubConst.GOOGLE_CLOUD_PROJECT);
  }
}
