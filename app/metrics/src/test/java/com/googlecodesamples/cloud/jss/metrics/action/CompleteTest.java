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
package com.googlecodesamples.cloud.jss.metrics.action;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.metrics.service.MetricPublisherService;
import com.googlecodesamples.cloud.jss.metrics.util.ActionTestUtil;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/** Unit test for {@link Complete}. */
public class CompleteTest {

  private static final String TOPIC_NAME = "test-topic";

  private MetricPublisherService service;

  private Publisher publisher;

  private Complete complete;

  @BeforeClass
  public static void checkRequirements() {
    try {
      assumeTrue(LogMessage.WARN_GCP_CREDENTIALS_NOT_SET, PubSubUtil.getGCPCredentials() != null);
    } catch (IOException e) {
      assumeNoException(e);
    }
  }

  @Before
  public void setUp() throws IOException {
    publisher = Publisher.newBuilder(TOPIC_NAME).build();
    service = new MetricPublisherService(publisher);
    complete = new Complete(service);
  }

  @After
  public void tearDown() {
    publisher.shutdown();
    service.shutdown();
  }

  @Test
  public void testRespond() throws IOException {
    PubsubMessage eventMessage = ActionTestUtil.genEventMessage(ActionTestUtil.genEvent());
    AckReplyConsumer consumer = Mockito.mock(AckReplyConsumer.class);
    Mockito.doNothing().when(consumer).ack();

    MetricsComplete message =
        complete.respond(
            consumer,
            eventMessage,
            ActionTestUtil.EXPECTED_PROCESS_TIME,
            ActionTestUtil.PUBLISH_TIME);

    assertThat(message.getEventTimestamp()).isEqualTo(ActionTestUtil.EXPECTED_SESSION_END_TIME);
    assertThat(message.getPublishTimestamp()).isEqualTo(ActionTestUtil.EXPECTED_PUBLISH_TIME);
    assertThat(message.getProcessingTimeSec()).isEqualTo(ActionTestUtil.EXPECTED_PROCESS_TIME);
    assertThat(message.getBatteryLevelEnd()).isEqualTo(ActionTestUtil.EXPECTED_BATTERY_LEVEL_END);
    assertThat(message.getChargedTotalKwh()).isEqualTo(ActionTestUtil.EXPECTED_CHARGED_TOTAL_KWH);
    assertThat(message.getAckTimestamp()).isAtLeast(ActionTestUtil.EXPECTED_ACK_TIME);
    assertThat(message.getSessionDurationHr() * 60)
        .isAtLeast(ActionTestUtil.EXPECTED_MIN_SESSION_MINUTES);
    assertThat(message.getSessionDurationHr() * 60)
        .isAtMost(ActionTestUtil.EXPECTED_MAX_SESSION_MINUTES);
  }
}
