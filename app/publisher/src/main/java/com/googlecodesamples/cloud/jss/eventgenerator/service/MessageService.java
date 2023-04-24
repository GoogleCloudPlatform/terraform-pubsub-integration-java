package com.googlecodesamples.cloud.jss.eventgenerator.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.googlecodesamples.cloud.jss.common.utilities.EvChargeEvent;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  public PubsubMessage toPubSubMessage(EvChargeEvent evChargeEvent) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Encoder encoder = EncoderFactory.get().jsonEncoder(EvChargeEvent.getClassSchema(), output);
      evChargeEvent.customEncode(encoder);
      encoder.flush();
      return PubsubMessage.newBuilder().setData(ByteString.copyFrom(output.toByteArray())).build();
    } catch (IOException e) {
      log.error("ToPubSubMessage IOException", e);
    }
    return null;
  }
}
