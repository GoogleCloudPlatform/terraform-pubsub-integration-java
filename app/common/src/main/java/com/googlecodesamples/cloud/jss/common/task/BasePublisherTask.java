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
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Asynchronous task that will be used by the publisher. */
public abstract class BasePublisherTask implements Runnable {

  private BasePublisherService service;

  public BasePublisherService getService() {
    return service;
  }

  public void setService(BasePublisherService service) {
    this.service = service;
  }

  protected abstract void doAsyncTask()
      throws InterruptedException, ExecutionException, IOException;

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        doAsyncTask();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (IOException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
