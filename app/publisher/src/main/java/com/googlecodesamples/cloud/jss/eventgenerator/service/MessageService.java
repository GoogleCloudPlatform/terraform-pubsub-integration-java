package com.googlecodesamples.cloud.jss.eventgenerator.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  public PubsubMessage toPubSubMessage(Event event) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().jsonEncoder(Event.getClassSchema(), output);
    DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(Event.getClassSchema());
    writer.write(event, encoder);
    encoder.flush();
    return PubsubMessage.newBuilder().setData(ByteString.copyFrom(output.toByteArray())).build();
  }
}
