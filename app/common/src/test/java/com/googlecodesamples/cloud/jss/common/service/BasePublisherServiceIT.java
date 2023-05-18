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
package com.googlecodesamples.cloud.jss.common.service;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.util.StringUtils;

/** Integration test for {@link BasePublisherService}. */
public class BasePublisherServiceIT {

  private static final Logger logger = LoggerFactory.getLogger(BasePublisherServiceIT.class);

  private static final String EXPECTED_MSG_PUBLISH = "topic";

  public static final String GOOGLE_CLOUD_PROJECT = System.getenv(PubSubConst.GOOGLE_CLOUD_PROJECT);

  private static final String TOPIC_NAME =
      String.format("projects/%s/topics/test-event-topic", GOOGLE_CLOUD_PROJECT);

  private static final String MESSAGE_CONTENT = "test";

  private Publisher publisher;

  @Rule public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @BeforeClass
  public static void checkRequirements() {
    assumeTrue(LogMessage.WARN_GCP_PROJECT_NOT_SET, StringUtils.hasText(GOOGLE_CLOUD_PROJECT));
  }

  @Before
  public void setUp() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(TOPIC_NAME).build();
      topicAdminClient.createTopic(topic);
    }

    publisher = Publisher.newBuilder(TOPIC_NAME).build();
  }

  @After
  public void tearDown() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(TOPIC_NAME);
    }

    publisher.shutdown();
  }

  @Test
  public void testPublishMsg() throws InterruptedException, ExecutionException {
    PubsubMessage message =
        PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(MESSAGE_CONTENT)).build();
    BasePublisherService publisherService = new SimplePublisherService(publisher);
    publisherService.publishMsg(message);
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_PUBLISH);
  }

  static class SimplePublisherService extends BasePublisherService {
    public SimplePublisherService(Publisher publisher) {
      super(publisher);
    }

    @Override
    public void init() {
      logger.info("initializing SimplePublisherService");
    }

    @Override
    public void shutdown() {
      logger.info("shutting down SimplePublisherService");
    }
  }
}
