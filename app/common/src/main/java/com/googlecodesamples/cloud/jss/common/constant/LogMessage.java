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

public class LogMessage {

  public static final String ERROR_NEGATIVE_VALUE = "should be greater than zero";

  public static final String ERROR_EMPTY_VALUE = "should not be empty";

  public static final String ERROR_EMPTY_NAME = "The topic/subscription name should not be empty";

  public static final String ERROR_NEGATIVE_THREADS =
      "The number of executor threads should be greater than zero";

  public static final String WARN_GCP_PROJECT_NOT_SET =
      String.format(
          "environment variable '%s' has not been set, test skipped.",
          PubSubConst.GOOGLE_CLOUD_PROJECT);

  public static final String WARN_GCP_LOCATION_NOT_SET =
      String.format(
          "environment variable '%s' has not been set, test skipped.",
          PubSubConst.GOOGLE_CLOUD_LOCATION);

  public static final String WARN_GCP_CREDENTIALS_NOT_SET =
      String.format(
          "environment variable '%s' has not been set, test skipped.",
          PubSubConst.GOOGLE_APPLICATION_CREDENTIALS);
}
