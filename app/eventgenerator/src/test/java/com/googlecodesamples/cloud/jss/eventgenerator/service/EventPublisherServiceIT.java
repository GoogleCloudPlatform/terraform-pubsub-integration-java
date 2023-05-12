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
package com.googlecodesamples.cloud.jss.eventgenerator.service;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.googlecodesamples.cloud.jss.eventgenerator.config.EventGeneratorConfig;
import java.io.IOException;
import org.junit.*;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.util.StringUtils;

/** Integration test for {@link EventPublisherService}. */
public class EventPublisherServiceIT {

  private static final String EXPECTED_MSG_PUBLISH = "settings for publishMsgAsync() times";

  private static final String EXPECTED_MSG_SHUTDOWN =
      "shutting down the thread pool and timer for EventPublisherService";

  public static final String GOOGLE_CLOUD_PROJECT = "GOOGLE_CLOUD_PROJECT";

  public static final String GOOGLE_CLOUD_LOCATION = "GOOGLE_CLOUD_LOCATION";

  public static final String ENV_GCP_PROJECT = System.getenv(GOOGLE_CLOUD_PROJECT);

  public static final String ENV_GCP_LOCATION = System.getenv(GOOGLE_CLOUD_LOCATION);

  private static final String WARN_MSG_GCP =
      "environment variable '%s' has not been set, test skipped.";

  private static final String EVENT_TOPIC_NAME =
      String.format("projects/%s/topics/test-event-topic", ENV_GCP_PROJECT);

  private EventPublisherService service;

  private Publisher publisher;

  @Rule public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @BeforeClass
  public static void checkRequirements() {
    assumeTrue(
        String.format(WARN_MSG_GCP, GOOGLE_CLOUD_PROJECT), StringUtils.hasText(ENV_GCP_PROJECT));
    assumeTrue(
        String.format(WARN_MSG_GCP, GOOGLE_CLOUD_LOCATION), StringUtils.hasText(ENV_GCP_LOCATION));
  }

  @Before
  public void setUp() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(EVENT_TOPIC_NAME).build();
      topicAdminClient.createTopic(topic);
    }

    publisher = Publisher.newBuilder(EVENT_TOPIC_NAME).build();
    EventGeneratorConfig config = new EventGeneratorConfig();
    service = new EventPublisherService(publisher, config);
  }

  @After
  public void tearDown() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(EVENT_TOPIC_NAME);
    }

    publisher.shutdown();
    service.shutdown();
  }

  @Test
  public void testPublisher() {
    // test publishMsgAsync
    int times = 1, thread = 1;
    float sleep = 1, executionTime = 1;

    service.publishMsgAsync(times, thread, sleep, executionTime);
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_PUBLISH);

    // test shutdown
    service.shutdown();
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_SHUTDOWN);
  }
}
