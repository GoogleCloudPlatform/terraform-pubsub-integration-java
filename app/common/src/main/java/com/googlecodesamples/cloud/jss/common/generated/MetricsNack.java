/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.googlecodesamples.cloud.jss.common.generated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class MetricsNack extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1748768842984907498L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"MetricsNack\",\"namespace\":\"com.googlecodesamples.cloud.jss.common.generated\",\"fields\":[{\"name\":\"session_id\",\"type\":\"string\"},{\"name\":\"station_id\",\"type\":\"int\"},{\"name\":\"location\",\"type\":\"string\"},{\"name\":\"event_timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-micros\"}},{\"name\":\"publish_timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-micros\"}},{\"name\":\"processing_time_sec\",\"type\":\"float\"},{\"name\":\"ack_timestamp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-micros\"}},{\"name\":\"session_duration_hr\",\"type\":\"float\"},{\"name\":\"avg_charge_rate_kw\",\"type\":\"float\"},{\"name\":\"battery_capacity_kwh\",\"type\":\"float\"},{\"name\":\"battery_level_start\",\"type\":\"float\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();
  static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMicrosConversion());
  }

  private static final BinaryMessageEncoder<MetricsNack> ENCODER =
      new BinaryMessageEncoder<MetricsNack>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<MetricsNack> DECODER =
      new BinaryMessageDecoder<MetricsNack>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<MetricsNack> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<MetricsNack> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<MetricsNack> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<MetricsNack>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this MetricsNack to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a MetricsNack from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a MetricsNack instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static MetricsNack fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence session_id;
  private int station_id;
  private java.lang.CharSequence location;
  private java.time.Instant event_timestamp;
  private java.time.Instant publish_timestamp;
  private float processing_time_sec;
  private java.time.Instant ack_timestamp;
  private float session_duration_hr;
  private float avg_charge_rate_kw;
  private float battery_capacity_kwh;
  private float battery_level_start;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public MetricsNack() {}

  /**
   * All-args constructor.
   * @param session_id The new value for session_id
   * @param station_id The new value for station_id
   * @param location The new value for location
   * @param event_timestamp The new value for event_timestamp
   * @param publish_timestamp The new value for publish_timestamp
   * @param processing_time_sec The new value for processing_time_sec
   * @param ack_timestamp The new value for ack_timestamp
   * @param session_duration_hr The new value for session_duration_hr
   * @param avg_charge_rate_kw The new value for avg_charge_rate_kw
   * @param battery_capacity_kwh The new value for battery_capacity_kwh
   * @param battery_level_start The new value for battery_level_start
   */
  public MetricsNack(java.lang.CharSequence session_id, java.lang.Integer station_id, java.lang.CharSequence location, java.time.Instant event_timestamp, java.time.Instant publish_timestamp, java.lang.Float processing_time_sec, java.time.Instant ack_timestamp, java.lang.Float session_duration_hr, java.lang.Float avg_charge_rate_kw, java.lang.Float battery_capacity_kwh, java.lang.Float battery_level_start) {
    this.session_id = session_id;
    this.station_id = station_id;
    this.location = location;
    this.event_timestamp = event_timestamp.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    this.publish_timestamp = publish_timestamp.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    this.processing_time_sec = processing_time_sec;
    this.ack_timestamp = ack_timestamp.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    this.session_duration_hr = session_duration_hr;
    this.avg_charge_rate_kw = avg_charge_rate_kw;
    this.battery_capacity_kwh = battery_capacity_kwh;
    this.battery_level_start = battery_level_start;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return session_id;
    case 1: return station_id;
    case 2: return location;
    case 3: return event_timestamp;
    case 4: return publish_timestamp;
    case 5: return processing_time_sec;
    case 6: return ack_timestamp;
    case 7: return session_duration_hr;
    case 8: return avg_charge_rate_kw;
    case 9: return battery_capacity_kwh;
    case 10: return battery_level_start;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  private static final org.apache.avro.Conversion<?>[] conversions =
      new org.apache.avro.Conversion<?>[] {
      null,
      null,
      null,
      new org.apache.avro.data.TimeConversions.TimestampMicrosConversion(),
      new org.apache.avro.data.TimeConversions.TimestampMicrosConversion(),
      null,
      new org.apache.avro.data.TimeConversions.TimestampMicrosConversion(),
      null,
      null,
      null,
      null,
      null
  };

  @Override
  public org.apache.avro.Conversion<?> getConversion(int field) {
    return conversions[field];
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: session_id = (java.lang.CharSequence)value$; break;
    case 1: station_id = (java.lang.Integer)value$; break;
    case 2: location = (java.lang.CharSequence)value$; break;
    case 3: event_timestamp = (java.time.Instant)value$; break;
    case 4: publish_timestamp = (java.time.Instant)value$; break;
    case 5: processing_time_sec = (java.lang.Float)value$; break;
    case 6: ack_timestamp = (java.time.Instant)value$; break;
    case 7: session_duration_hr = (java.lang.Float)value$; break;
    case 8: avg_charge_rate_kw = (java.lang.Float)value$; break;
    case 9: battery_capacity_kwh = (java.lang.Float)value$; break;
    case 10: battery_level_start = (java.lang.Float)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'session_id' field.
   * @return The value of the 'session_id' field.
   */
  public java.lang.CharSequence getSessionId() {
    return session_id;
  }


  /**
   * Sets the value of the 'session_id' field.
   * @param value the value to set.
   */
  public void setSessionId(java.lang.CharSequence value) {
    this.session_id = value;
  }

  /**
   * Gets the value of the 'station_id' field.
   * @return The value of the 'station_id' field.
   */
  public int getStationId() {
    return station_id;
  }


  /**
   * Sets the value of the 'station_id' field.
   * @param value the value to set.
   */
  public void setStationId(int value) {
    this.station_id = value;
  }

  /**
   * Gets the value of the 'location' field.
   * @return The value of the 'location' field.
   */
  public java.lang.CharSequence getLocation() {
    return location;
  }


  /**
   * Sets the value of the 'location' field.
   * @param value the value to set.
   */
  public void setLocation(java.lang.CharSequence value) {
    this.location = value;
  }

  /**
   * Gets the value of the 'event_timestamp' field.
   * @return The value of the 'event_timestamp' field.
   */
  public java.time.Instant getEventTimestamp() {
    return event_timestamp;
  }


  /**
   * Sets the value of the 'event_timestamp' field.
   * @param value the value to set.
   */
  public void setEventTimestamp(java.time.Instant value) {
    this.event_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
  }

  /**
   * Gets the value of the 'publish_timestamp' field.
   * @return The value of the 'publish_timestamp' field.
   */
  public java.time.Instant getPublishTimestamp() {
    return publish_timestamp;
  }


  /**
   * Sets the value of the 'publish_timestamp' field.
   * @param value the value to set.
   */
  public void setPublishTimestamp(java.time.Instant value) {
    this.publish_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
  }

  /**
   * Gets the value of the 'processing_time_sec' field.
   * @return The value of the 'processing_time_sec' field.
   */
  public float getProcessingTimeSec() {
    return processing_time_sec;
  }


  /**
   * Sets the value of the 'processing_time_sec' field.
   * @param value the value to set.
   */
  public void setProcessingTimeSec(float value) {
    this.processing_time_sec = value;
  }

  /**
   * Gets the value of the 'ack_timestamp' field.
   * @return The value of the 'ack_timestamp' field.
   */
  public java.time.Instant getAckTimestamp() {
    return ack_timestamp;
  }


  /**
   * Sets the value of the 'ack_timestamp' field.
   * @param value the value to set.
   */
  public void setAckTimestamp(java.time.Instant value) {
    this.ack_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
  }

  /**
   * Gets the value of the 'session_duration_hr' field.
   * @return The value of the 'session_duration_hr' field.
   */
  public float getSessionDurationHr() {
    return session_duration_hr;
  }


  /**
   * Sets the value of the 'session_duration_hr' field.
   * @param value the value to set.
   */
  public void setSessionDurationHr(float value) {
    this.session_duration_hr = value;
  }

  /**
   * Gets the value of the 'avg_charge_rate_kw' field.
   * @return The value of the 'avg_charge_rate_kw' field.
   */
  public float getAvgChargeRateKw() {
    return avg_charge_rate_kw;
  }


  /**
   * Sets the value of the 'avg_charge_rate_kw' field.
   * @param value the value to set.
   */
  public void setAvgChargeRateKw(float value) {
    this.avg_charge_rate_kw = value;
  }

  /**
   * Gets the value of the 'battery_capacity_kwh' field.
   * @return The value of the 'battery_capacity_kwh' field.
   */
  public float getBatteryCapacityKwh() {
    return battery_capacity_kwh;
  }


  /**
   * Sets the value of the 'battery_capacity_kwh' field.
   * @param value the value to set.
   */
  public void setBatteryCapacityKwh(float value) {
    this.battery_capacity_kwh = value;
  }

  /**
   * Gets the value of the 'battery_level_start' field.
   * @return The value of the 'battery_level_start' field.
   */
  public float getBatteryLevelStart() {
    return battery_level_start;
  }


  /**
   * Sets the value of the 'battery_level_start' field.
   * @param value the value to set.
   */
  public void setBatteryLevelStart(float value) {
    this.battery_level_start = value;
  }

  /**
   * Creates a new MetricsNack RecordBuilder.
   * @return A new MetricsNack RecordBuilder
   */
  public static com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder newBuilder() {
    return new com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder();
  }

  /**
   * Creates a new MetricsNack RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new MetricsNack RecordBuilder
   */
  public static com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder newBuilder(com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder other) {
    if (other == null) {
      return new com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder();
    } else {
      return new com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder(other);
    }
  }

  /**
   * Creates a new MetricsNack RecordBuilder by copying an existing MetricsNack instance.
   * @param other The existing instance to copy.
   * @return A new MetricsNack RecordBuilder
   */
  public static com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder newBuilder(com.googlecodesamples.cloud.jss.common.generated.MetricsNack other) {
    if (other == null) {
      return new com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder();
    } else {
      return new com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder(other);
    }
  }

  /**
   * RecordBuilder for MetricsNack instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<MetricsNack>
    implements org.apache.avro.data.RecordBuilder<MetricsNack> {

    private java.lang.CharSequence session_id;
    private int station_id;
    private java.lang.CharSequence location;
    private java.time.Instant event_timestamp;
    private java.time.Instant publish_timestamp;
    private float processing_time_sec;
    private java.time.Instant ack_timestamp;
    private float session_duration_hr;
    private float avg_charge_rate_kw;
    private float battery_capacity_kwh;
    private float battery_level_start;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.session_id)) {
        this.session_id = data().deepCopy(fields()[0].schema(), other.session_id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.station_id)) {
        this.station_id = data().deepCopy(fields()[1].schema(), other.station_id);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.location)) {
        this.location = data().deepCopy(fields()[2].schema(), other.location);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.event_timestamp)) {
        this.event_timestamp = data().deepCopy(fields()[3].schema(), other.event_timestamp);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.publish_timestamp)) {
        this.publish_timestamp = data().deepCopy(fields()[4].schema(), other.publish_timestamp);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.processing_time_sec)) {
        this.processing_time_sec = data().deepCopy(fields()[5].schema(), other.processing_time_sec);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.ack_timestamp)) {
        this.ack_timestamp = data().deepCopy(fields()[6].schema(), other.ack_timestamp);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.session_duration_hr)) {
        this.session_duration_hr = data().deepCopy(fields()[7].schema(), other.session_duration_hr);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.avg_charge_rate_kw)) {
        this.avg_charge_rate_kw = data().deepCopy(fields()[8].schema(), other.avg_charge_rate_kw);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
      if (isValidValue(fields()[9], other.battery_capacity_kwh)) {
        this.battery_capacity_kwh = data().deepCopy(fields()[9].schema(), other.battery_capacity_kwh);
        fieldSetFlags()[9] = other.fieldSetFlags()[9];
      }
      if (isValidValue(fields()[10], other.battery_level_start)) {
        this.battery_level_start = data().deepCopy(fields()[10].schema(), other.battery_level_start);
        fieldSetFlags()[10] = other.fieldSetFlags()[10];
      }
    }

    /**
     * Creates a Builder by copying an existing MetricsNack instance
     * @param other The existing instance to copy.
     */
    private Builder(com.googlecodesamples.cloud.jss.common.generated.MetricsNack other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.session_id)) {
        this.session_id = data().deepCopy(fields()[0].schema(), other.session_id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.station_id)) {
        this.station_id = data().deepCopy(fields()[1].schema(), other.station_id);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.location)) {
        this.location = data().deepCopy(fields()[2].schema(), other.location);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.event_timestamp)) {
        this.event_timestamp = data().deepCopy(fields()[3].schema(), other.event_timestamp);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.publish_timestamp)) {
        this.publish_timestamp = data().deepCopy(fields()[4].schema(), other.publish_timestamp);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.processing_time_sec)) {
        this.processing_time_sec = data().deepCopy(fields()[5].schema(), other.processing_time_sec);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.ack_timestamp)) {
        this.ack_timestamp = data().deepCopy(fields()[6].schema(), other.ack_timestamp);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.session_duration_hr)) {
        this.session_duration_hr = data().deepCopy(fields()[7].schema(), other.session_duration_hr);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.avg_charge_rate_kw)) {
        this.avg_charge_rate_kw = data().deepCopy(fields()[8].schema(), other.avg_charge_rate_kw);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.battery_capacity_kwh)) {
        this.battery_capacity_kwh = data().deepCopy(fields()[9].schema(), other.battery_capacity_kwh);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.battery_level_start)) {
        this.battery_level_start = data().deepCopy(fields()[10].schema(), other.battery_level_start);
        fieldSetFlags()[10] = true;
      }
    }

    /**
      * Gets the value of the 'session_id' field.
      * @return The value.
      */
    public java.lang.CharSequence getSessionId() {
      return session_id;
    }


    /**
      * Sets the value of the 'session_id' field.
      * @param value The value of 'session_id'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setSessionId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.session_id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'session_id' field has been set.
      * @return True if the 'session_id' field has been set, false otherwise.
      */
    public boolean hasSessionId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'session_id' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearSessionId() {
      session_id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'station_id' field.
      * @return The value.
      */
    public int getStationId() {
      return station_id;
    }


    /**
      * Sets the value of the 'station_id' field.
      * @param value The value of 'station_id'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setStationId(int value) {
      validate(fields()[1], value);
      this.station_id = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'station_id' field has been set.
      * @return True if the 'station_id' field has been set, false otherwise.
      */
    public boolean hasStationId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'station_id' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearStationId() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'location' field.
      * @return The value.
      */
    public java.lang.CharSequence getLocation() {
      return location;
    }


    /**
      * Sets the value of the 'location' field.
      * @param value The value of 'location'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setLocation(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.location = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'location' field has been set.
      * @return True if the 'location' field has been set, false otherwise.
      */
    public boolean hasLocation() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'location' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearLocation() {
      location = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'event_timestamp' field.
      * @return The value.
      */
    public java.time.Instant getEventTimestamp() {
      return event_timestamp;
    }


    /**
      * Sets the value of the 'event_timestamp' field.
      * @param value The value of 'event_timestamp'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setEventTimestamp(java.time.Instant value) {
      validate(fields()[3], value);
      this.event_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'event_timestamp' field has been set.
      * @return True if the 'event_timestamp' field has been set, false otherwise.
      */
    public boolean hasEventTimestamp() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'event_timestamp' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearEventTimestamp() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'publish_timestamp' field.
      * @return The value.
      */
    public java.time.Instant getPublishTimestamp() {
      return publish_timestamp;
    }


    /**
      * Sets the value of the 'publish_timestamp' field.
      * @param value The value of 'publish_timestamp'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setPublishTimestamp(java.time.Instant value) {
      validate(fields()[4], value);
      this.publish_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'publish_timestamp' field has been set.
      * @return True if the 'publish_timestamp' field has been set, false otherwise.
      */
    public boolean hasPublishTimestamp() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'publish_timestamp' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearPublishTimestamp() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'processing_time_sec' field.
      * @return The value.
      */
    public float getProcessingTimeSec() {
      return processing_time_sec;
    }


    /**
      * Sets the value of the 'processing_time_sec' field.
      * @param value The value of 'processing_time_sec'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setProcessingTimeSec(float value) {
      validate(fields()[5], value);
      this.processing_time_sec = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'processing_time_sec' field has been set.
      * @return True if the 'processing_time_sec' field has been set, false otherwise.
      */
    public boolean hasProcessingTimeSec() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'processing_time_sec' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearProcessingTimeSec() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'ack_timestamp' field.
      * @return The value.
      */
    public java.time.Instant getAckTimestamp() {
      return ack_timestamp;
    }


    /**
      * Sets the value of the 'ack_timestamp' field.
      * @param value The value of 'ack_timestamp'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setAckTimestamp(java.time.Instant value) {
      validate(fields()[6], value);
      this.ack_timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'ack_timestamp' field has been set.
      * @return True if the 'ack_timestamp' field has been set, false otherwise.
      */
    public boolean hasAckTimestamp() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'ack_timestamp' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearAckTimestamp() {
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'session_duration_hr' field.
      * @return The value.
      */
    public float getSessionDurationHr() {
      return session_duration_hr;
    }


    /**
      * Sets the value of the 'session_duration_hr' field.
      * @param value The value of 'session_duration_hr'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setSessionDurationHr(float value) {
      validate(fields()[7], value);
      this.session_duration_hr = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'session_duration_hr' field has been set.
      * @return True if the 'session_duration_hr' field has been set, false otherwise.
      */
    public boolean hasSessionDurationHr() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'session_duration_hr' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearSessionDurationHr() {
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'avg_charge_rate_kw' field.
      * @return The value.
      */
    public float getAvgChargeRateKw() {
      return avg_charge_rate_kw;
    }


    /**
      * Sets the value of the 'avg_charge_rate_kw' field.
      * @param value The value of 'avg_charge_rate_kw'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setAvgChargeRateKw(float value) {
      validate(fields()[8], value);
      this.avg_charge_rate_kw = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'avg_charge_rate_kw' field has been set.
      * @return True if the 'avg_charge_rate_kw' field has been set, false otherwise.
      */
    public boolean hasAvgChargeRateKw() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'avg_charge_rate_kw' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearAvgChargeRateKw() {
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'battery_capacity_kwh' field.
      * @return The value.
      */
    public float getBatteryCapacityKwh() {
      return battery_capacity_kwh;
    }


    /**
      * Sets the value of the 'battery_capacity_kwh' field.
      * @param value The value of 'battery_capacity_kwh'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setBatteryCapacityKwh(float value) {
      validate(fields()[9], value);
      this.battery_capacity_kwh = value;
      fieldSetFlags()[9] = true;
      return this;
    }

    /**
      * Checks whether the 'battery_capacity_kwh' field has been set.
      * @return True if the 'battery_capacity_kwh' field has been set, false otherwise.
      */
    public boolean hasBatteryCapacityKwh() {
      return fieldSetFlags()[9];
    }


    /**
      * Clears the value of the 'battery_capacity_kwh' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearBatteryCapacityKwh() {
      fieldSetFlags()[9] = false;
      return this;
    }

    /**
      * Gets the value of the 'battery_level_start' field.
      * @return The value.
      */
    public float getBatteryLevelStart() {
      return battery_level_start;
    }


    /**
      * Sets the value of the 'battery_level_start' field.
      * @param value The value of 'battery_level_start'.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder setBatteryLevelStart(float value) {
      validate(fields()[10], value);
      this.battery_level_start = value;
      fieldSetFlags()[10] = true;
      return this;
    }

    /**
      * Checks whether the 'battery_level_start' field has been set.
      * @return True if the 'battery_level_start' field has been set, false otherwise.
      */
    public boolean hasBatteryLevelStart() {
      return fieldSetFlags()[10];
    }


    /**
      * Clears the value of the 'battery_level_start' field.
      * @return This builder.
      */
    public com.googlecodesamples.cloud.jss.common.generated.MetricsNack.Builder clearBatteryLevelStart() {
      fieldSetFlags()[10] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MetricsNack build() {
      try {
        MetricsNack record = new MetricsNack();
        record.session_id = fieldSetFlags()[0] ? this.session_id : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.station_id = fieldSetFlags()[1] ? this.station_id : (java.lang.Integer) defaultValue(fields()[1]);
        record.location = fieldSetFlags()[2] ? this.location : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.event_timestamp = fieldSetFlags()[3] ? this.event_timestamp : (java.time.Instant) defaultValue(fields()[3]);
        record.publish_timestamp = fieldSetFlags()[4] ? this.publish_timestamp : (java.time.Instant) defaultValue(fields()[4]);
        record.processing_time_sec = fieldSetFlags()[5] ? this.processing_time_sec : (java.lang.Float) defaultValue(fields()[5]);
        record.ack_timestamp = fieldSetFlags()[6] ? this.ack_timestamp : (java.time.Instant) defaultValue(fields()[6]);
        record.session_duration_hr = fieldSetFlags()[7] ? this.session_duration_hr : (java.lang.Float) defaultValue(fields()[7]);
        record.avg_charge_rate_kw = fieldSetFlags()[8] ? this.avg_charge_rate_kw : (java.lang.Float) defaultValue(fields()[8]);
        record.battery_capacity_kwh = fieldSetFlags()[9] ? this.battery_capacity_kwh : (java.lang.Float) defaultValue(fields()[9]);
        record.battery_level_start = fieldSetFlags()[10] ? this.battery_level_start : (java.lang.Float) defaultValue(fields()[10]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<MetricsNack>
    WRITER$ = (org.apache.avro.io.DatumWriter<MetricsNack>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<MetricsNack>
    READER$ = (org.apache.avro.io.DatumReader<MetricsNack>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}









