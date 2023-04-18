package com.cienet.pub.model;

import com.cienet.pub.util.PubUtil;
import com.cienet.pub.utilities.EvChargeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseEvChargeEvent {
  private static final Logger log = LoggerFactory.getLogger(BaseEvChargeEvent.class);

  private final String LOCATION_ENV = "GOOGLE_CLOUD_LOCATION";
  private List<Integer> AVG_CHARGE_RATE_KW = Arrays.asList(20, 72, 100, 120, 150);
  private final float BIAS = 1 / 100f;
  private final List<Integer> BATTERY_CAPACITY_KWH =
      Arrays.asList(40, 50, 58, 62, 75, 77, 82, 100, 129, 131);
  private String session_id;
  private int station_id;
  private String location;
  private String session_start_time;
  private String session_end_time;
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

  public String getSession_start_time() {
    return session_start_time;
  }

  public void setSession_start_time(String session_start_time) {
    this.session_start_time = session_start_time;
  }

  public String getSession_end_time() {
    return session_end_time;
  }

  public void setSession_end_time(String session_end_time) {
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

  public EvChargeEvent convert2Avro() {
    return EvChargeEvent.newBuilder()
        .setSessionId(getSession_id())
        .setStationId(getStation_id())
        .setLocation(getLocation())
        .setSessionStartTime(getSession_start_time())
        .setSessionEndTime(getSession_end_time())
        .setAvgChargeRateKw(getAvg_charge_rate_kw())
        .setBatteryCapacityKwh(getBattery_capacity_kwh())
        .setBatteryLevelStart(getBattery_level_start())
        .build();
  }

  public void genRandomData() {
    setSession_id(UUID.randomUUID().toString());
    int stationIdRan = PubUtil.genRandomInt(0, 100);
    setStation_id(stationIdRan);
    //    setLocation(System.getenv(LOCATION_ENV));
    // TODO
    setLocation("us-west");
    long endTime = System.currentTimeMillis();
    int randomInt = PubUtil.genRandomInt(5, 90);
    long startTime = endTime - randomInt * 60 * 1000L;
    setSession_start_time(PubUtil.formatTime(startTime));
    setSession_end_time(PubUtil.formatTime(endTime));
    int avgChargeRateKwIndexRan = stationIdRan % AVG_CHARGE_RATE_KW.size();
    int plusMinusIndexRan = stationIdRan % 2;
    float avgChargeRate = AVG_CHARGE_RATE_KW.get(avgChargeRateKwIndexRan);
    if (plusMinusIndexRan == 0) {
      avgChargeRate += BIAS;
    } else {
      avgChargeRate -= BIAS;
    }
    setAvg_charge_rate_kw(avgChargeRate);
    int batteryCapacityKwhIndexRan = stationIdRan % BATTERY_CAPACITY_KWH.size();
    setBattery_capacity_kwh(BATTERY_CAPACITY_KWH.get(batteryCapacityKwhIndexRan));
    setBattery_level_start(PubUtil.genRandomFloat(0.05f, 0.8f));
  }
}
