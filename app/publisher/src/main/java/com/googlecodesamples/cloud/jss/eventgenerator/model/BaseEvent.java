package com.googlecodesamples.cloud.jss.eventgenerator.model;

import com.googlecodesamples.cloud.jss.common.util.PubSubUtil;
import com.googlecodesamples.cloud.jss.common.utilities.Event;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEvent {
  private static final Logger log = LoggerFactory.getLogger(BaseEvent.class);
  private static final String GOOGLE_CLOUD_LOCATION = "GOOGLE_CLOUD_LOCATION";
  private final List<Integer> AVG_CHARGE_RATE_KW = Arrays.asList(20, 72, 100, 120, 250);
  private final List<Integer> BATTERY_CAPACITY_KWH =
      Arrays.asList(40, 50, 58, 62, 75, 77, 82, 100, 129, 131);
  private final float BIAS = 1 / 100f;

  private String session_id;
  private int station_id;
  private String location;
  private long session_start_time;
  private long session_end_time;
  private float avg_charge_rate_kw;
  private float battery_capacity_kwh;
  private float battery_level_start;

  public String getSession_id() {
    return session_id;
  }

  public void setSession_id(String session_id) {
    this.session_id = session_id;
  }

  public int getStation_id() {
    return station_id;
  }

  public void setStation_id(int station_id) {
    this.station_id = station_id;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public long getSession_start_time() {
    return session_start_time;
  }

  public void setSession_start_time(long session_start_time) {
    this.session_start_time = session_start_time;
  }

  public long getSession_end_time() {
    return session_end_time;
  }

  public void setSession_end_time(long session_end_time) {
    this.session_end_time = session_end_time;
  }

  public float getAvg_charge_rate_kw() {
    return avg_charge_rate_kw;
  }

  public void setAvg_charge_rate_kw(float avg_charge_rate_kw) {
    this.avg_charge_rate_kw = avg_charge_rate_kw;
  }

  public float getBattery_capacity_kwh() {
    return battery_capacity_kwh;
  }

  public void setBattery_capacity_kwh(float battery_capacity_kwh) {
    this.battery_capacity_kwh = battery_capacity_kwh;
  }

  public float getBattery_level_start() {
    return battery_level_start;
  }

  public void setBattery_level_start(float battery_level_start) {
    this.battery_level_start = battery_level_start;
  }

  public Event convert2Avro() {
    return Event.newBuilder()
        .setSessionId(getSession_id())
        .setStationId(getStation_id())
        .setLocation(getLocation())
        .setSessionStartTime(Instant.ofEpochSecond(getSession_start_time()))
        .setSessionEndTime(Instant.ofEpochSecond(getSession_end_time()))
        .setAvgChargeRateKw(getAvg_charge_rate_kw())
        .setBatteryCapacityKwh(getBattery_capacity_kwh())
        .setBatteryLevelStart(getBattery_level_start())
        .build();
  }

  public void genRandomData() {
    setSession_id(UUID.randomUUID().toString());
    setStation_id(genStationId());
    setLocation(System.getenv(GOOGLE_CLOUD_LOCATION));
    long sessionEndTime = Instant.now().getEpochSecond();
    setSession_start_time(genSessionStartTime(sessionEndTime));
    setSession_end_time(sessionEndTime);
    setAvg_charge_rate_kw(genAvChargeRateKw());
    setBattery_capacity_kwh(genBatteryCapacityKwh());
    setBattery_level_start(genBatteryLevelStart());
  }

  private int genStationId() {
    return PubSubUtil.genRandomInt(0, 100);
  }

  private long genSessionStartTime(long sessionEndTime) {
    int processTime = PubSubUtil.genRandomInt(5, 90);
    return (sessionEndTime - processTime * 60L);
  }

  private float genAvChargeRateKw() {
    int index = PubSubUtil.genRandomInt(0, AVG_CHARGE_RATE_KW.size() - 1);
    float avgChargeRateKw = AVG_CHARGE_RATE_KW.get(index);
    if (index % 2 == 0) {
      avgChargeRateKw += BIAS;
    } else {
      avgChargeRateKw -= BIAS;
    }
    return avgChargeRateKw;
  }

  private float genBatteryCapacityKwh() {
    return BATTERY_CAPACITY_KWH.get(PubSubUtil.genRandomInt(0, BATTERY_CAPACITY_KWH.size() - 1));
  }

  private float genBatteryLevelStart() {
    return PubSubUtil.genRandomFloat(0.05f, 0.8f);
  }
}
