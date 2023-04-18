package com.cienet.pub.service;

import com.cienet.pub.publisher.IPublisher;
import com.cienet.pub.task.PublishTask;
import com.cienet.pub.utilities.EvChargeEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
  private static final Logger log = LoggerFactory.getLogger(PublishService.class);

  private final IPublisher publisher;

  private ExecutorService executor = null;

  public PublishService(IPublisher publisher) {
    this.publisher = publisher;
  }

  public void randomPublishMsg(int times, int thread, float sleep) {
    executor = Executors.newFixedThreadPool(thread);
    for (int i = 0; i < thread; i++) {
      executor.execute(new PublishTask(times, publisher, sleep));
    }
  }

  public void publishMsg(EvChargeEvent evChargeEvent) {
    publisher.publish(evChargeEvent);
  }

  public void shutdownRandom() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }
}
