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

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class EventPublisherServiceIT {

  public static final String GOOGLE_CLOUD_PROJECT = System.getenv("GOOGLE_CLOUD_PROJECT");

  public static final String GOOGLE_CLOUD_LOCATION = System.getenv("GOOGLE_CLOUD_LOCATION");

  private static final String MSG_PUBLISH_RANDOM_SETTING = "settings for publishMsgAsync() times";

  private static final String MSG_SHUTDOWN_RANDOM =
      "shutting down the thread pool and timer for EventPublisherService";

  @Autowired
  EventPublisherService service;
  @Rule
  public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @Value("${event.publisher.topic_name}")
  private String topicName;

  @BeforeClass
  public static void checkRequirements() {
    assertThat(GOOGLE_CLOUD_PROJECT).isNotNull();
    assertThat(GOOGLE_CLOUD_LOCATION).isNotNull();
  }

  @Before
  public void setUp() throws Exception {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(topicName).build();
      topicAdminClient.createTopic(topic);
    }
  }

  @After
  public void tearDown() throws Exception {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(topicName);
    }
  }

  @Test
  public void testPublisher() {
    // test publishMsgAsync
    int times = 1, thread = 1;
    float sleep = 1, executionTime = 1;
    service.publishMsgAsync(times, thread, sleep, executionTime);
    assertThat(outputCapture.getOut()).contains(MSG_PUBLISH_RANDOM_SETTING);

    // test shutdown
    service.shutdown();
    assertThat(outputCapture.getOut()).contains(MSG_SHUTDOWN_RANDOM);
  }
}
