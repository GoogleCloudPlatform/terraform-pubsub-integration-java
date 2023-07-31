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
package com.googlecodesamples.cloud.jss.eventgenerator.controller;

import com.googlecodesamples.cloud.jss.eventgenerator.service.EventPublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;

/** REST API controller for the {@link EventPublisherService} instance. */
@RestController
@RequestMapping("/api/msg")
public class EventPublisherController {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherController.class);

  private final EventPublisherService service;

  public EventPublisherController(EventPublisherService service) {
    this.service = service;
  }

  /**
   * An API endpoint to generate and publish messages to Google's Cloud Pub/Sub topic.
   * When the application is started, the {@link EventPublisherService#startPublishMsgAsync} method is called to start publishing messages automatically.
   * If the user wants to publish messages manually after the application is fully started,
   * this API endpoint can be used to restart the task based on user's desired parameters.
   * <br><br>
   * The threads and runtime parameters are optional. If not specified, the default values are 1 and 1 respectively.
   * <br>
   * <li> Modify the "threads" for the number of threads to use (default is 1).
   * <li> Modify the "runtime" for the runtime of the task (in minute).
   *
   * @param threads number of thread
   * @param runtime time to execute the task (in minute)
   */
  @PostMapping("/random")
  public void publishMsgRandom(
      @RequestParam(required = false, defaultValue = "1") int threads,
      @RequestParam(required = false, defaultValue = "1") float runtime) {
    logger.info("entering publishMsgRandom()");
    service.publishMsgAsync(threads, runtime);
  }

  /**
   * An API endpoint to shut down the {@link EventPublisherService} immediately
   * by stopping all actively executing tasks. It utilizes Java's {@link ExecutorService#shutdown()} method,
   * which does not wait for actively executing tasks to terminate. Use it with caution since it may cause data loss.
   */
  @PostMapping("/shutdown")
  public void shutdown() {
    logger.info("entering shutdown()");
    service.shutdown();
  }
}
