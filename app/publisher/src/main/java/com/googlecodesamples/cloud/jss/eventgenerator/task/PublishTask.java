package com.googlecodesamples.cloud.jss.eventgenerator.task;

import com.googlecodesamples.cloud.jss.eventgenerator.publisher.IPublisher;
import com.googlecodesamples.cloud.jss.eventgenerator.model.BaseEvChargeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PublishTask.class);

    private final int count;

    private final float sleep;

    private final IPublisher publisher;

    public PublishTask(int count, IPublisher publisher, float sleep) {
        this.count = count;
        this.publisher = publisher;
        this.sleep = sleep;
    }

    @Override
    public void run() {
        if (count >= 0) {
            for (int i = 0; i < count; i++) {
                sendMessage();
            }
        } else {
            while (!Thread.currentThread().isInterrupted()) {
                sendMessage();
            }
        }
    }

    private void sendMessage() {
        try {
            BaseEvChargeEvent baseEvChargeEvent = new BaseEvChargeEvent();
            baseEvChargeEvent.genRandomData();
            publisher.publish(baseEvChargeEvent.convert2Avro());
            Thread.sleep((long) (sleep * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted");
        }
    }
}
