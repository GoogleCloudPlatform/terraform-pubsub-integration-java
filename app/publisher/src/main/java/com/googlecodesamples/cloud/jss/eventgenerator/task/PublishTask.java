package com.googlecodesamples.cloud.jss.eventgenerator.task;

import com.googlecodesamples.cloud.jss.eventgenerator.model.BaseEvent;
import com.googlecodesamples.cloud.jss.eventgenerator.service.PublishService;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
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
    try {
      if (isSendInfinitely()) {
        while (!Thread.currentThread().isInterrupted()) {
          sendMessage();
        }
      } else {
        for (int i = 0; i < this.count; i++) {
          sendMessage();
        }
      }
    } catch (InterruptedException | ExecutionException | IOException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void sendMessage() throws InterruptedException, ExecutionException, IOException {
    BaseEvent event = new BaseEvent();
    event.genRandomData();
    publishService.publishMsg(event.convert2Avro());
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
