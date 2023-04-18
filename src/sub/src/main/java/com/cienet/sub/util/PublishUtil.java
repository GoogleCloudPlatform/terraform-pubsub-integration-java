package com.cienet.sub.util;

import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishUtil {
  private static final Logger log = LoggerFactory.getLogger(PublishUtil.class);

  public static ByteString jsonEncode(SpecificRecordBase avroRecord, Schema schema) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Encoder encoder = EncoderFactory.get().jsonEncoder(schema, byteArrayOutputStream);
      avroRecord.customEncode(encoder);
      encoder.flush();
      return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
    } catch (Exception e) {
      log.error("JsonEncode error", e);
    }
    return null;
  }
}
