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

package com.googlecodesamples.cloud.jss.metrics.service;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import com.googlecodesamples.cloud.jss.common.constant.LogMessage;
import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.metrics.action.Complete;
import com.googlecodesamples.cloud.jss.metrics.config.EventSubscriberConfig;
import com.googlecodesamples.cloud.jss.metrics.factory.EventSubscriberFactory;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.util.StringUtils;

/** Integration tests for {@link EventSubscriberService}. */
public class EventSubscriberServiceIT {

  private static final String EXPECTED_MSG_CONTENT = "test";

  private static final String EXPECTED_MSG_CALLBACK = "callback received";

  public static final String ENV_GCP_PROJECT = PubSubUtil.getEnvProjectId();

  private static final String TOPIC_NAME = "test-event-topic";

  private static final String SUBSCRIPTION_NAME = "test-subscription";

  private static TopicName EVENT_TOPIC;

  private static ProjectSubscriptionName EVENT_SUBSCRIPTION;

  private MetricPublisherService publisherService;

  private EventSubscriberService subscriberService;

  private Publisher publisher;

  private Subscriber subscriber;

  @Rule public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @BeforeClass
  public static void checkRequirements() {
    assumeTrue(LogMessage.WARN_GCP_PROJECT_NOT_SET, StringUtils.hasText(ENV_GCP_PROJECT));

    EVENT_TOPIC = TopicName.of(ENV_GCP_PROJECT, TOPIC_NAME);
    EVENT_SUBSCRIPTION = ProjectSubscriptionName.of(ENV_GCP_PROJECT, SUBSCRIPTION_NAME);
  }

  @Before
  public void setUp() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(EVENT_TOPIC.toString()).build();
      topicAdminClient.createTopic(topic);
    }

    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
      Subscription subscription =
          Subscription.newBuilder()
              .setName(EVENT_SUBSCRIPTION.toString())
              .setTopic(EVENT_TOPIC.toString())
              .build();
      subscriptionAdminClient.createSubscription(subscription);
    }

    publisher = Publisher.newBuilder(EVENT_TOPIC.toString()).build();
    publisherService = new MetricPublisherService(publisher);

    Complete complete = new Complete(publisherService);
    EventSubscriberFactory factory = new EventSubscriberFactory(createSubscriberConfig(), complete);
    subscriber = factory.createSubscriber();
    subscriberService = new EventSubscriberService(subscriber);
    subscriberService.startSubscriberAsync();
  }

  @After
  public void tearDown() throws IOException {
    publisher.shutdown();
    subscriber.stopAsync();
    subscriberService.cleanUp();
    publisherService.shutdown();

    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
      subscriptionAdminClient.deleteSubscription(EVENT_SUBSCRIPTION.toString());
    }

    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(EVENT_TOPIC);
    }
  }

  @Test
  public void testSubscriber() throws InterruptedException, ExecutionException {
    publishMessage();
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_CONTENT);
    assertThat(outputCapture.getOut()).contains(EXPECTED_MSG_CALLBACK);
  }

  private void publishMessage() throws InterruptedException, ExecutionException {
    PubsubMessage message =
        PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(EXPECTED_MSG_CONTENT)).build();
    publisherService.publishMsg(message);
  }

  private EventSubscriberConfig createSubscriberConfig() {
    EventSubscriberConfig config = new EventSubscriberConfig();
    config.setEventSubscription(SUBSCRIPTION_NAME);
    config.setOutstandingMessages(100L);
    config.setParallelPull(1);
    config.setExecutorThreads(2);
    return config;
  }
}
