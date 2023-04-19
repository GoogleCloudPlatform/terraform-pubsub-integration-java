package com.googlecodesamples.cloud.jss.eventgenerator.task;

import com.googlecodesamples.cloud.jss.eventgenerator.model.BaseEvChargeEvent;
import com.googlecodesamples.cloud.jss.eventgenerator.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishTask implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(PublishTask.class);

  private static final float MIN_SLEEP = 0.2f;

  private static final int INFINITE_FLAG = -1;

  private int count;

  private float sleep;

  private final PublishService publishService;

  public PublishTask(PublishService publishService, float sleep, int count) {
    this.publishService = publishService;
    this.sleep = sleep;
    this.count = count;
    validate();
  }

  @Override
  public void run() {
    if (isSendInfinitely()) {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          sendMessage();
        } catch (InterruptedException e) {
          break;
        }
      }
    } else {
      for (int i = 0; i < this.count; i++) {
        try {
          sendMessage();
        } catch (InterruptedException e) {
          break;
        }
      }
    }
  }

  private void sendMessage() throws InterruptedException {
    BaseEvChargeEvent baseEvChargeEvent = new BaseEvChargeEvent();
    baseEvChargeEvent.genRandomData();
    publishService.publishMsg(baseEvChargeEvent.convert2Avro());
    Thread.sleep((long) (sleep * 1000));
  }

  private void validate() {
    this.count = Math.max(this.count, INFINITE_FLAG);
    this.sleep = Math.max(this.sleep, MIN_SLEEP);
  }

  private boolean isSendInfinitely() {
    return this.count == INFINITE_FLAG;
  }
}
