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

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

/** Unit test for {@link BaseConfig}. */
public class BaseConfigTest {

  private static final List<Integer> NEGATIVE_INPUTS = Arrays.asList(0, -1, -10);

  private static final String ERROR_MSG_NEGATIVE_INPUT = "should be greater than zero";

  private static final String ERROR_MSG_EMPTY_INPUT = "should not be empty";

  private BaseConfig config;

  protected BaseConfig getConfig() {
    return this.config;
  }

  @Before
  public void setUp() {
    config = Mockito.mock(BaseConfig.class, Answers.CALLS_REAL_METHODS);
  }

  @Test
  public void testIllegalExecutorThreadNumber() {
    for (Integer input : NEGATIVE_INPUTS) {
      try {
        getConfig().setExecutorThreads(input);
      } catch (IllegalArgumentException e) {
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
        assertThat(e).hasMessageThat().contains(ERROR_MSG_NEGATIVE_INPUT);
      }
    }
  }

  @Test
  public void testNullQueueName() {
    try {
      getConfig().checkEmptyName(null);
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }

  @Test
  public void testEmptyQueueName() {
    try {
      getConfig().checkEmptyName("");
    } catch (IllegalArgumentException e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class);
      assertThat(e).hasMessageThat().contains(ERROR_MSG_EMPTY_INPUT);
    }
  }
}