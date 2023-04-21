# app-large-data-sharing-java

## Dockerized a Spring Boot application
```bash
// under pub dir
export PUB_IMAGE_NAME=<your image, eg. gcr.io/my-project/spring-boot-jib>
mvn compile com.google.cloud.tools:jib-maven-plugin:3.3.1:build

// under sub dir
export SUB_IMAGE_NAME=<your image, eg. gcr.io/my-project/spring-boot-jib>
mvn compile com.google.cloud.tools:jib-maven-plugin:3.3.1:build
```

## Environment Variable
### Publisher
```bash
EVENT_TOPIC=<EventTopic topic name>
GOOGLE_CLOUD_LOCATION=<GKE deployment’s location>
RETRY_TOTAL_TIMEOUT=<Retry total timeout in second, default 600>
RETRY_INITIAL_RPC_TIMEOUT=<Retry initial timeout in second, default 5>
RETRY_MAX_RPC_TIMEOUT=<Retry max rpc timeout in second, default 600>
PUBLISHER_EXECUTOR_THREADS=<Publisher executor thread number, default 4>
FLOW_CONTROL_MAX_OUTSTANDING_ELEMENT=<Outstanding element, default 100>
FLOW_CONTROL_MAX_OUTSTANDING_REQUEST=<Outstanding request in MB, default 10>
BATCH_ELEMENT_COUNT_THRESHOLD=<Element threshold, default 1>
BATCH_REQUEST_BYTE_THRESHOLD=<Request threshold in bytes, default 100>
BATCH_DELAY_THRESHOLD=<Delay threshold in millisecond, default 1>
```

### Subscriber
```bash
EVENT_SUBSCRIPTION=<EventSubscription subscription name>
METRIC_TOPIC=<MetricTopic topic name>
METRIC_APP_PORT=<App rest port, default 8001>
RETRY_TOTAL_TIMEOUT=<Retry total timeout in second, default 600>
RETRY_INITIAL_RPC_TIMEOUT=<Retry initial timeout in second, default 5>
RETRY_MAX_RPC_TIMEOUT=<Retry max rpc timeout in second, default 600>
FLOW_CONTROL_MAX_OUTSTANDING_ELEMENT=<Outstanding element, default 100>
FLOW_CONTROL_MAX_OUTSTANDING_REQUEST=<Outstanding request in MB, default 10>
BATCH_ELEMENT_COUNT_THRESHOLD=<Element threshold, default 100>
BATCH_REQUEST_BYTE_THRESHOLD=<Request threshold in bytes, default 1000>
BATCH_DELAY_THRESHOLD=<Delay threshold in millisecond, default 1>
PARALLEL_PULL_COUNT=<Parallel count, default 1>
PUBLISHER_EXECUTOR_THREADS=<Publisher executor thread number, default 4>
SUBSCRIBER_EXECUTOR_THREADS=<Subscriber executor thread number, default 4>
```

## Api
### Publisher
#### 1. Publish a message
```bash
[Post] api/msg

{
  "session_id": "21c4f020-3918-4aa1-8fa4-204c8a56ff26",
  "station_id": 67,
  "location": "us-west1",
  "session_start_time": "2023-04-20T07:11:00Z",
  "session_end_time": "2023-04-20T07:47:00Z",
  "avg_charge_rate_kw": 99.99,
  "battery_capacity_kwh": 100.0,
  "battery_level_start": 0.6
}
```

#### 2. Publish a random generate message
| Parameter | Type  | Default | Comment                                                    |
|-----------|-------|---------|------------------------------------------------------------|
| times     | int   | -1      | number of msg each thread publish (-1 means infinite loop) |
| thread    | int   | 1       | number of thread                                           |
| sleep     | float | 1       | time to sleep after each message (in second)               |

```bash
[POST] api/msg/random?times=30&thread=2
```

#### 3. Shutdown msg publishing

```bash
[POST] api/msg/shutdown
```

## TODO
1. Avro time, Complete null

