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
import com.google.pubsub.v1.TopicName;
import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.eventgenerator.config.EventGeneratorConfig;
import java.io.IOException;
import org.junit.*;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.util.StringUtils;

/** Integration test for {@link EventPublisherService}. */
public class EventPublisherServiceIT {

  private static final String EXPECTED_MSG_PUBLISH = "settings for publishMsgAsync()";

  private static final String EXPECTED_MSG_SHUTDOWN =
      "shutting down the thread pool and timer for EventPublisherService";

  public static final String ENV_GCP_LOCATION = System.getenv(PubSubConst.GOOGLE_CLOUD_LOCATION);

  public static final String ENV_GCP_PROJECT = PubSubUtil.getEnvProjectId();

  private static final String TOPIC_NAME = "test-event-topic";

  private static TopicName EVENT_TOPIC;

  private EventPublisherService service;

  private Publisher publisher;

  @Rule public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @BeforeClass
  public static void checkRequirements() {
    assumeTrue(LogMessage.WARN_GCP_PROJECT_NOT_SET, StringUtils.hasText(ENV_GCP_PROJECT));
    assumeTrue(LogMessage.WARN_GCP_LOCATION_NOT_SET, StringUtils.hasText(ENV_GCP_LOCATION));

    EVENT_TOPIC = TopicName.of(ENV_GCP_PROJECT, TOPIC_NAME);
  }

  @Before
  public void setUp() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(EVENT_TOPIC.toString()).build();
      topicAdminClient.createTopic(topic);
    }

    publisher = Publisher.newBuilder(EVENT_TOPIC).build();
    EventGeneratorConfig config = new EventGeneratorConfig();
    service = new EventPublisherService(publisher, config);
  }

  @After
  public void tearDown() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(EVENT_TOPIC);
    }

    publisher.shutdown();
    service.shutdown();
  }

  @Test
  public void testPublisher() {
    // test publishMsgAsync
    int threads = 1;
    float runtime = 1;

    service.publishMsgAsync(threads, runtime);
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_PUBLISH);

    // test shutdown
    service.shutdown();
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_SHUTDOWN);
  }
}
