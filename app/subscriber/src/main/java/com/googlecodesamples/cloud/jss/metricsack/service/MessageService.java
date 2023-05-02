package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  public PubsubMessage toPubSubMessage(Object payload, Schema schema) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().jsonEncoder(schema, output);
    DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(schema);
    writer.write((SpecificRecordBase) payload, encoder);
    encoder.flush();
    return PubsubMessage.newBuilder().setData(ByteString.copyFrom(output.toByteArray())).build();
  }

  public <T> T fromPubSubMessage(PubsubMessage message) throws IOException {
    Schema schema = Event.getClassSchema();
    SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
    InputStream inputStream = new ByteArrayInputStream(message.getData().toByteArray());
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
    return reader.read(null, decoder);
  }
}
