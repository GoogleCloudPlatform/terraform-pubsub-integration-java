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
package com.googlecodesamples.cloud.jss.eventgenerator.task;

import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.constant.PubSubConst;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import com.googlecodesamples.cloud.jss.common.task.BasePublisherTask;
import com.googlecodesamples.cloud.jss.common.util.MessageUtil;
import com.googlecodesamples.cloud.jss.eventgenerator.service.EventPublisherService;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Implementation of the asynchronous task to publish a message to Pub/Sub. */
public class MessageTask extends BasePublisherTask {

  public MessageTask(EventPublisherService service, Float sleep, Integer count) {
    setService(service);
    setSleep(Math.max(sleep, PubSubConst.MIN_SLEEP_INTERVAL));
    setCount(Math.max(count, PubSubConst.INFINITE_FLAG));
  }

  @Override
  protected void doAsyncTask() throws InterruptedException, ExecutionException, IOException {
    Event event = MessageUtil.genRandomEvent();
    PubsubMessage message = MessageUtil.convertToPubSubMessage(event, Event.getClassSchema());
    BasePublisherService service = getService();

    service.publishMsg(message);
    Thread.sleep((long) (getSleep() * 1000));
  }
}
