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

import com.googlecodesamples.cloud.jss.common.task.BasePublisherTimerTask;
import com.googlecodesamples.cloud.jss.eventgenerator.service.EventPublisherService;

/** Implementation of the timer task to shut down the event publisher thread pool. */
public class TimeoutTask extends BasePublisherTimerTask {

  public TimeoutTask(EventPublisherService service) {
    setService(service);
  }

  @Override
  protected void doScheduledTask() {
    getService().shutdown();
  }
}
