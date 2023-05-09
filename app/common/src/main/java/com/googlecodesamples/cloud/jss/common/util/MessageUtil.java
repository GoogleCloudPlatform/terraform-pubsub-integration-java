/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecodesamples.cloud.jss.common.util;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecodesamples.cloud.jss.common.generated.Event;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

/**
 * Reusable utility functions for creating message.
 */
public class MessageUtil {

  private static final List<Integer> AVG_CHARGE_RATE_KW = Arrays.asList(20, 72, 100, 120, 250);
  private static final List<Integer> BATTERY_CAPACITY_KWH =
          Arrays.asList(40, 50, 58, 62, 75, 77, 82, 100, 129, 131);
  private static final Integer MIN_STATION_ID = 0;
  private static final Integer MAX_STATION_ID = 100;
  private static final Integer MIN_SESSION_DURATION = 5;
  private static final Integer MAX_SESSION_DURATION = 90;
  private static final Float MIN_BATTERY_PERCENTAGE = 0.05f;
  private static final Float MAX_BATTERY_PERCENTAGE = 0.8f;
  private static final Float BIAS = 1 / 100f;

  private static final String LOCATIONS = System.getenv("GOOGLE_CLOUD_LOCATION");

  /**
   * Generate random stationId.
   *
   * @return random stationId
   */
  public static int genStationId() {
    return PubSubUtil.genRandomInt(MIN_STATION_ID, MAX_STATION_ID);
  }

  /**
   * Generate session start time.
   *
   * @param sessionEndTime session end time
   * @return session start time
   */
  public static long genSessionStartTime(long sessionEndTime) {
    int durationSeconds = PubSubUtil.genRandomInt(MIN_SESSION_DURATION, MAX_SESSION_DURATION);
    return (sessionEndTime - durationSeconds * 60L);
  }

  /**
   * Generate random average charging speed
   *
   * @return random average charging speed
   */
  public static float genAvChargeRateKw() {
    int index = PubSubUtil.genRandomInt(0, AVG_CHARGE_RATE_KW.size() - 1);
    float avgChargeRateKw = AVG_CHARGE_RATE_KW.get(index);
    if (index % 2 == 0) {
      avgChargeRateKw += BIAS;
    } else {
      avgChargeRateKw -= BIAS;
    }
    return avgChargeRateKw;
  }

  /**
   * Generate random total vehicle battery capacity.
   *
   * @return random total vehicle battery capacity
   */
  public static float genBatteryCapacityKwh() {
    return BATTERY_CAPACITY_KWH.get(PubSubUtil.genRandomInt(0, BATTERY_CAPACITY_KWH.size() - 1));
  }

  /**
   * Generate random battery charge percentage at the beginning of the session.
   *
   * @return random battery charge percentage
   */
  public static float genBatteryLevelStart() {
    return PubSubUtil.genRandomFloat(MIN_BATTERY_PERCENTAGE, MAX_BATTERY_PERCENTAGE);
  }

  /**
   * Generate random event data.
   */
  public static Event genRandomEvent() {
    long sessionEndTime = Instant.now().getEpochSecond();
    return Event.newBuilder()
            .setSessionId(UUID.randomUUID().toString())
            .setStationId(genStationId())
            .setLocation(LOCATIONS)
            .setSessionStartTime(Instant.ofEpochSecond(genSessionStartTime(sessionEndTime)))
            .setSessionEndTime(Instant.ofEpochSecond(sessionEndTime))
            .setAvgChargeRateKw(genAvChargeRateKw())
            .setBatteryCapacityKwh(genBatteryCapacityKwh())
            .setBatteryLevelStart(genBatteryLevelStart())
            .build();
  }

  /**
   * Convert an Avro object to GCP Pub/Sub compatible format.
   *
   * @param message message to be published
   * @param schema  Avro schema of the message
   * @return encoded message
   */
  public static PubsubMessage convertToPubSubMessage(Object message, Schema schema) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().jsonEncoder(schema, output);
    DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(schema);
    writer.write((SpecificRecordBase) message, encoder);
    encoder.flush();
    return PubsubMessage.newBuilder().setData(ByteString.copyFrom(output.toByteArray())).build();
  }

  /**
   * Convert a GCP Pub/Sub message to Avro object.
   *
   * @param message message received from GCP pub/sub
   * @return decoded Avro object
   */
  public static Event convertToAvroEvent(PubsubMessage message) throws IOException {
    Schema schema = Event.getClassSchema();
    SpecificDatumReader<Event> reader = new SpecificDatumReader<>(schema);
    InputStream inputStream = new ByteArrayInputStream(message.getData().toByteArray());
    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
    return reader.read(null, decoder);
  }
}
