package com.cienet.pub.util;

import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishUtil {
  private static final Logger log = LoggerFactory.getLogger(PublishUtil.class);
  private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);
  private static final Random random = new Random();

  public static ByteString jsonEncode(SpecificRecordBase avroRecord, Schema schema) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Encoder encoder = EncoderFactory.get().jsonEncoder(schema, byteArrayOutputStream);
      avroRecord.customEncode(encoder);
      encoder.flush();
      return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
    } catch (IOException e) {
      log.error("JsonEncode error", e);
    }
    return null;
  }

  public static synchronized String formatTime(long currentTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    return DATE_FORMAT.format(currentTime);
  }

  public static int genRandomInt(int min, int max) {
    return random.nextInt((max - min + 1)) + min;
  }

  public static float genRandomFloat(float min, float max) {
    return random.nextFloat() * (max - min) + min;
  }
}
