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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/** Asynchronous task that will be used by the publisher. */
public abstract class BasePublisherTask implements Runnable {

  public static final Integer INFINITE_FLAG = -1;

  public static final Float MIN_SLEEP_INTERVAL = 0.2f;

  private BasePublisherService service;

  private Integer count;

  private Float sleep;

  public BasePublisherService getService() {
    return service;
  }

  public void setService(BasePublisherService service) {
    this.service = service;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Float getSleep() {
    return sleep;
  }

  public void setSleep(Float sleep) {
    this.sleep = sleep;
  }

  protected abstract void doAsyncTask()
      throws InterruptedException, ExecutionException, IOException;

  @Override
  public void run() {
    try {
      if (isRunInfinitely()) {
        while (!Thread.currentThread().isInterrupted()) {
          doAsyncTask();
        }
      } else {
        for (int i = 0; i < getCount(); i++) {
          doAsyncTask();
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (IOException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isRunInfinitely() {
    return Objects.equals(getCount(), INFINITE_FLAG);
  }
}
