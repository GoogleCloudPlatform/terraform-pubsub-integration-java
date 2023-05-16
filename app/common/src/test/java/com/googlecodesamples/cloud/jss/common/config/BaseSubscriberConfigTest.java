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
package com.googlecodesamples.cloud.jss.common.config;

import static com.google.common.truth.Truth.assertThat;

import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

/** Unit test for {@link BaseSubscriberConfig}. */
public class BaseSubscriberConfigTest {

  private static final List<Integer> NEGATIVE_INPUTS = Arrays.asList(0, -1, -10);

  private BaseSubscriberConfig config;

  protected BaseSubscriberConfig getConfig() {
    return this.config;
  }

  @Before
  public void setUp() {
    config = Mockito.mock(BaseSubscriberConfig.class, Answers.CALLS_REAL_METHODS);
  }

  @Test
  public void testIllegalExecutorThreadNumber() {
    for (Integer input : NEGATIVE_INPUTS) {
      try {
        getConfig().setExecutorThreads(input);
      } catch (IllegalArgumentException e) {
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
        assertThat(e).hasMessageThat().contains(LogMessage.ERROR_NEGATIVE_VALUE);
      }
    }
  }

  @Test
  public void testNullEventSubscription() {
    try {
      getConfig().setEventSubscription(null);
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(LogMessage.ERROR_EMPTY_VALUE);
    }
  }

  @Test
  public void testEmptyEventSubscription() {
    try {
      getConfig().setEventSubscription("");
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(LogMessage.ERROR_EMPTY_VALUE);
    }
  }
}
