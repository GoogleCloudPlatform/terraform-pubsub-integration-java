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

/** REST API controller of the backend service. */
@RestController
@RequestMapping("/api/msg")
public class EventPublisherController {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherController.class);

  private final EventPublisherService service;

  public EventPublisherController(EventPublisherService service) {
    this.service = service;
  }

  /**
   * Publish random messages.
   *
   * @param times number of message each thread publish
   * @param thread number of thread
   * @param sleep time to sleep after each message (in second)
   * @param executionTime time to execute the task (in minute)
   */
  @PostMapping("/random")
  public void publishMsgRandom(
      @RequestParam(required = false, defaultValue = "-1") int times,
      @RequestParam(required = false, defaultValue = "1") int thread,
      @RequestParam(required = false, defaultValue = "1") float sleep,
      @RequestParam(required = false, defaultValue = "-1") float executionTime) {
    logger.info("entering publishMsgRandom()");
    service.publishMsgAsync(times, thread, sleep, executionTime);
  }

  /** Shutdown threadPool and timer. */
  @PostMapping("/shutdown")
  public void shutdown() {
    logger.info("entering shutdown()");
    service.shutdown();
  }
}
