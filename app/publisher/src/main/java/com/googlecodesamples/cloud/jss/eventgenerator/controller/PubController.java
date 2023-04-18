package com.googlecodesamples.cloud.jss.eventgenerator.controller;

import com.googlecodesamples.cloud.jss.eventgenerator.model.BaseEvChargeEvent;
import com.googlecodesamples.cloud.jss.eventgenerator.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/msg")
public class PubController {
  private static final Logger log = LoggerFactory.getLogger(PubController.class);

  private final PublishService publishService;

  public PubController(PublishService publishService) {
    this.publishService = publishService;
  }

  /** Publish a message. */
  @PostMapping("")
  public void publishMsg(@RequestBody BaseEvChargeEvent baseEvChargeEvent) {
    publishService.publishMsg(baseEvChargeEvent.convert2Avro());
  }

  /** Publish random message. */
  @PostMapping("/random")
  public void randomPublishMsg(
      @RequestParam(required = false, defaultValue = "-1") int times,
      @RequestParam(required = false, defaultValue = "1") int thread,
      @RequestParam(required = false, defaultValue = "1") float sleep) {
    publishService.randomPublishMsg(times, thread, sleep);
  }

  /** Shutdown threadPool. */
  @PostMapping("/shutdown")
  public void shutdownRandom() {
    publishService.shutdownRandom();
  }
}
