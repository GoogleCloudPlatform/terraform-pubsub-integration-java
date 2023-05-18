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
package com.googlecodesamples.cloud.jss.common.constant;

import java.math.RoundingMode;

public class PubSubConst {

  public static final Integer SCALE = 2;

  public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  public static final String METRICS_ACK = "MetricsAck";

  public static final String METRICS_NACK = "MetricsNack";

  public static final String METRICS_COMPLETE = "MetricsComplete";

  public static final String GOOGLE_CLOUD_LOCATION = "GOOGLE_CLOUD_LOCATION";

  public static final String GOOGLE_CLOUD_PROJECT = "GOOGLE_CLOUD_PROJECT";

  public static final String GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";

  public static final Float BATTERY_LEVEL_END = 1.0f;

  public static final Integer INITIAL_TOTAL_MESSAGE = 0;
}
