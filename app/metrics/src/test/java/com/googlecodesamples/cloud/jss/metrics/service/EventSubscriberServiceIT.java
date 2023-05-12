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
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import com.googlecodesamples.cloud.jss.common.action.BaseAction;
import com.googlecodesamples.cloud.jss.common.generated.MetricsComplete;
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

  private static final String EXPECTED_MESSAGE_CONTENT = "test";

  private static final String EXPECTED_METRIC_APP = "MetricsComplete";

  private static final String GOOGLE_CLOUD_PROJECT = System.getenv("GOOGLE_CLOUD_PROJECT");

  private static final String eventTopic =
      String.format("projects/%s/topics/test-event-topic", GOOGLE_CLOUD_PROJECT);

  private static final String eventSubscription =
      String.format("projects/%s/subscriptions/test-subscription", GOOGLE_CLOUD_PROJECT);

  private static final String WARN_MSG_GCP_PROJECT =
      "environment variable 'GOOGLE_CLOUD_PROJECT' has not been set, test skipped.";

  private Publisher publisher;

  private MetricPublisherService publisherService;

  private EventSubscriberService subscriberService;

  @Rule public OutputCaptureRule outputCapture = new OutputCaptureRule();

  @BeforeClass
  public static void checkRequirements() {
    assumeTrue(WARN_MSG_GCP_PROJECT, StringUtils.hasText(GOOGLE_CLOUD_PROJECT));
  }

  @Before
  public void setUp() throws IOException {
    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      Topic topic = Topic.newBuilder().setName(eventTopic).build();
      topicAdminClient.createTopic(topic);
    }

    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
      Subscription subscription =
          Subscription.newBuilder().setName(eventSubscription).setTopic(eventTopic).build();
      subscriptionAdminClient.createSubscription(subscription);
    }

    publisher = Publisher.newBuilder(eventTopic).build();
    publisherService = new MetricPublisherService(publisher);
    EventSubscriberFactory factory = new EventSubscriberFactory(createSubscriberConfig());
    BaseAction<MetricsComplete> completeMetric = new Complete(publisherService);
    subscriberService =
        new EventSubscriberService(factory, null, null, completeMetric, EXPECTED_METRIC_APP);
  }

  @After
  public void tearDown() throws IOException {
    publisher.shutdown();
    subscriberService.cleanUp();
    publisherService.shutdown();

    try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
      subscriptionAdminClient.deleteSubscription(eventSubscription);
    }

    try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
      topicAdminClient.deleteTopic(eventTopic);
    }
  }

  @Test
  public void testSubscriber() throws InterruptedException, ExecutionException {
    assumeTrue(WARN_MSG_GCP_PROJECT, StringUtils.hasText(GOOGLE_CLOUD_PROJECT));

    publishMessage();
    assertThat(outputCapture.getOut()).contains(EXPECTED_METRIC_APP);
    assertThat(outputCapture.getOut()).contains(EXPECTED_MESSAGE_CONTENT);
  }

  private void publishMessage() throws InterruptedException, ExecutionException {
    PubsubMessage message =
        PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(EXPECTED_MESSAGE_CONTENT))
            .build();
    publisherService.publishMsg(message);
  }

  private EventSubscriberConfig createSubscriberConfig() {
    EventSubscriberConfig config = new EventSubscriberConfig();
    config.setEventSubscription(eventSubscription);
    config.setOutstandingMessages(100L);
    config.setParallelPull(1);
    config.setExecutorThreads(2);
    return config;
  }
}
