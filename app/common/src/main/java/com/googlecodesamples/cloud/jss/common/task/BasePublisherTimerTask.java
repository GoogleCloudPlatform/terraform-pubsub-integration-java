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
package com.googlecodesamples.cloud.jss.common.task;

import com.googlecodesamples.cloud.jss.common.service.BasePublisherService;
import java.util.TimerTask;

/** Scheduled timer task that will be used by the publisher. */
public abstract class BasePublisherTimerTask extends TimerTask {

  private BasePublisherService service;

  protected abstract void doScheduledTask();

  public BasePublisherService getService() {
    return service;
  }

  public void setService(BasePublisherService service) {
    this.service = service;
  }

  @Override
  public void run() {
    doScheduledTask();
  }
}
