package com.googlecodesamples.cloud.jss.metricsack.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricAck;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricComplete;
import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeMetricNack;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  @Value("${metric.app.type}")
  private String metricAppType;

  public PubsubMessage toPubSubMessage(Object payload) {
    try {
      Schema schema = null;
      switch (metricAppType) {
        case SubscribeService.METRICS_ACK:
          schema = EvChargeMetricAck.getClassSchema();
          break;
        case SubscribeService.METRICS_NACK:
          schema = EvChargeMetricNack.getClassSchema();
          break;
        case SubscribeService.METRICS_COMPLETE:
          schema = EvChargeMetricComplete.getClassSchema();
          break;
      }
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Encoder encoder = EncoderFactory.get().jsonEncoder(schema, output);
      ((SpecificRecordBase) payload).customEncode(encoder);
      encoder.flush();
      return PubsubMessage.newBuilder().setData(ByteString.copyFrom(output.toByteArray())).build();
    } catch (IOException e) {
      log.error("ToPubSubMessage IOException", e);
    }
    return null;
  }

  public <T> T fromPubSubMessage(PubsubMessage message) {
    try {
      Schema schema = EvChargeEvent.getClassSchema();
      SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
      InputStream inputStream = new ByteArrayInputStream(message.getData().toByteArray());
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
      return reader.read(null, decoder);
    } catch (IOException e) {
      log.error("FromPubSubMessage IOException", e);
    }
    return null;
  }
}
