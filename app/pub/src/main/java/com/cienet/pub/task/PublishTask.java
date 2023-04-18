package com.cienet.pub.task;

import com.cienet.pub.model.BaseEvChargeEvent;
import com.cienet.pub.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import java.util.concurrent.ExecutionException;

public class PublishTask implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(PublishTask.class);

  private final int times;
  private final float sleep;
  private final PublishService publishService;

  public PublishTask(int times, float sleep, PublishService publishService) {
    this.times = times;
    this.sleep = sleep;
    this.publishService = publishService;
  }

  @Override
  public void run() {
    if (times >= 0) {
      for (int i = 0; i < times; i++) {
        genMsg();
      }
    } else {
      while (!Thread.currentThread().isInterrupted()) {
        genMsg();
      }
    }
  }

  private void genMsg() {
    try {
      BaseEvChargeEvent baseEvChargeEvent = new BaseEvChargeEvent();
      baseEvChargeEvent.genRandomData();
      publishService.publishMsg(baseEvChargeEvent.convert2Avro());
      Thread.sleep((long) sleep * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException("InterruptedException", e);
    } catch (ExecutionException e) {
      log.error("ExecutionException", e);
    }
  }
}
