package com.googlecodesamples.cloud.jss.metricsack.util;

import com.googlecodesamples.cloud.jss.metricsack.utilities.EvChargeEvent;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubUtil {
  private static final Logger log = LoggerFactory.getLogger(PubSubUtil.class);
  private static final Random random = new Random();
  private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

  public static EvChargeEvent jsonDecode(ByteString data, Schema schema) {
    try {
      SpecificDatumReader<EvChargeEvent> reader = new SpecificDatumReader<>(schema);
      InputStream inputStream = new ByteArrayInputStream(data.toByteArray());
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
      return reader.read(null, decoder);
    } catch (Exception e) {
      log.error("JsonDecode error", e);
    }
    return null;
  }

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

  public static float genRandomFloat(float min, float max) {
    return random.nextFloat() * (max - min) + min;
  }

  public static synchronized String formatTime(long currentTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    return DATE_FORMAT.format(currentTime);
  }

  public static synchronized float getDiffTimeInHour(String endTime, String startTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    float diffHour = -1;
    try {
      diffHour =
          (DATE_FORMAT.parse(endTime).getTime() - DATE_FORMAT.parse(startTime).getTime())
              / (1000f * 60 * 60);
    } catch (Exception e) {
      log.error("getDiffTimeInHour error", e);
    }
    return diffHour;
  }
}
