package com.googlecodesamples.cloud.jss.eventgenerator.converter;

import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.eventgenerator.utilities.EvChargeEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaseMessageConverter implements PubSubMessageConverter {
  private static final Logger log = LoggerFactory.getLogger(BaseMessageConverter.class);

  @Override
  public PubsubMessage toPubSubMessage(Object payload, Map<String, String> headers) {
    try {
      EvChargeEvent evChargeEvent = (EvChargeEvent) payload;
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

  @Override
  public <T> T fromPubSubMessage(PubsubMessage message, Class<T> payloadType) {
    return null;
  }
}
