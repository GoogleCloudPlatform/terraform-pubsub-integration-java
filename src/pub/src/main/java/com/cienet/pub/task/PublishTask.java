package com.cienet.pub.task;

import com.cienet.pub.model.BaseEvChargeEvent;
import com.cienet.pub.publisher.IPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishTask implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(PublishTask.class);

  private int times;
  private float sleep;
  private final IPublisher publisher;

  public PublishTask(int times, IPublisher publisher, float sleep) {
    this.times = times;
    this.publisher = publisher;
    this.sleep = sleep;
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
      publisher.publish(baseEvChargeEvent.convert2Avro());
      Thread.sleep((long) sleep * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted");
    }
  }
}
