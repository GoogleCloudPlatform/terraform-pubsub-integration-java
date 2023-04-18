package com.cienet.sub.util;

import com.cienet.sub.utilities.EvChargeEvent;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscribeUtil {
  private static final Logger log = LoggerFactory.getLogger(SubscribeUtil.class);
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

  public static float genRandomFloat(float min, float max) {
    return random.nextFloat() * (max - min) + min;
  }

  public static synchronized String formatTime(long currentTime) {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    return DATE_FORMAT.format(currentTime);
  }

  public static synchronized long parseTime(String time) {
    try {
      DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
      return DATE_FORMAT.parse(time).getTime();
    } catch (ParseException e) {
      log.error("ParseTime error", e);
    }
    return -1;
  }

  public static float getDiffTimeInHour(String endTime, String startTime) {
    return (parseTime(endTime) - parseTime(startTime)) / (1000f * 60 * 60);
  }
}
