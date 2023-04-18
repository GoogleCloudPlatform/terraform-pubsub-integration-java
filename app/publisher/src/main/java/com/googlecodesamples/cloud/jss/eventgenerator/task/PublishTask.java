package com.googlecodesamples.cloud.jss.eventgenerator.task;

import com.googlecodesamples.cloud.jss.eventgenerator.publisher.IPublisher;
import com.googlecodesamples.cloud.jss.eventgenerator.model.BaseEvChargeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PublishTask.class);

    private static final float MIN_SLEEP = 0.2f;

    private static final int INFINITE_FLAG = -1;

    private int count;

    private float sleep;

    private final IPublisher publisher;

    public PublishTask(int count, IPublisher publisher, float sleep) {
        this.count = count;
        this.publisher = publisher;
        this.sleep = sleep;
        validate();
    }

    @Override
    public void run() {
        if (isSendInfinitely()) {
            while (!Thread.currentThread().isInterrupted()) {
                sendMessage();
            }
        } else {
            for (int i = 0; i < this.count; i++) {
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

    private boolean isSendInfinitely() {
        return this.count == INFINITE_FLAG;
    }

    private void validate() {
        this.count = Math.max(this.count, INFINITE_FLAG);
        this.sleep = Math.max(this.count, MIN_SLEEP);
    }
}
