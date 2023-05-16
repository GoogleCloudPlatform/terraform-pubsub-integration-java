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
package com.googlecodesamples.cloud.jss.metrics.util;

import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import java.math.BigDecimal;

public class ActionUtil {

  /**
   * Calculate battery charge percentage at the end of the session
   *
   * @param batteryChargeStart battery charge percentage at the beginning of the session
   * @param chargingSpeed battery charge percentage at the beginning of the session
   * @param batteryCapacity total vehicle battery capacity, measured in KiloWattHours
   * @param duration session duration in hours
   * @return battery level end value
   */
  public static float genBatteryLevelEnd(
      float batteryChargeStart, float chargingSpeed, float batteryCapacity, float duration) {
    BigDecimal batteryLevelStart = BigDecimal.valueOf(batteryChargeStart);
    BigDecimal avgChargeRateKw = BigDecimal.valueOf(chargingSpeed);
    BigDecimal batteryCapacityKwh = BigDecimal.valueOf(batteryCapacity);
    BigDecimal sessionDurationHr = BigDecimal.valueOf(duration);

    BigDecimal result =
        batteryLevelStart.add(
            avgChargeRateKw
                .multiply(sessionDurationHr)
                .divide(batteryCapacityKwh, PubSubConst.SCALE, PubSubConst.ROUNDING_MODE));

    float batteryLevelEnd = PubSubUtil.formatFloat(result.floatValue());
    return Math.min(PubSubConst.BATTERY_LEVEL_END, batteryLevelEnd);
  }

  /**
   * Calculate battery charge percentage at the end of the session
   *
   * @param message MetricsComplete message
   * @return battery level end value
   */
  public static float genBatteryLevelEnd(MetricsComplete message) {
    return genBatteryLevelEnd(
        message.getBatteryLevelStart(),
        message.getAvgChargeRateKw(),
        message.getBatteryCapacityKwh(),
        message.getSessionDurationHr());
  }

  /**
   * Calculate total battery charged
   *
   * @param batteryChargeStart battery charge percentage at the beginning of the session
   * @param batteryChargeEnd battery charge percentage at the end of the session
   * @param batteryCapacity total vehicle battery capacity, measured in KiloWattHours
   * @return total battery charged
   */
  public static float genChargedTotalKwh(
      float batteryChargeStart, float batteryChargeEnd, float batteryCapacity) {
    BigDecimal batteryLevelEnd = BigDecimal.valueOf(batteryChargeEnd);
    BigDecimal batteryLevelStart = BigDecimal.valueOf(batteryChargeStart);
    BigDecimal batteryCapacityKwh = BigDecimal.valueOf(batteryCapacity);

    BigDecimal chargedTotalKwh =
        (batteryLevelEnd.subtract(batteryLevelStart)).multiply(batteryCapacityKwh);
    return PubSubUtil.formatFloat(chargedTotalKwh.floatValue());
  }

  /**
   * Calculate total battery charged
   *
   * @param message MetricsComplete message
   * @return total battery charged
   */
  public static float genChargedTotalKwh(MetricsComplete message) {
    if (message.getBatteryLevelEnd() == null) {
      message.setBatteryLevelEnd(genBatteryLevelEnd(message));
    }
    return genChargedTotalKwh(
        message.getBatteryLevelStart(),
        message.getBatteryLevelEnd(),
        message.getBatteryCapacityKwh());
  }
}
