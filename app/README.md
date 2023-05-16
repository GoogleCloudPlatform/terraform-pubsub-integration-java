# app-large-data-sharing-java

## Avro Codegen

```bash
# Please ensure the generated avro files exist or run the following command to generate
mvn avro:schema
```

## Quick testing

```bash
# [SetUp]
export GOOGLE_CLOUD_LOCATION=<your region>
export GOOGLE_CLOUD_PROJECT=<your project-id>

# [Unit Test]
mvn test -Punit-test

# [Integration Test]
# Please set up your cloud infrastructure before running the integration test
# 1. Cloud Storage and Firestore database must be enabled first
# 2. Set up gcloud cli tools on your environment
# 3. Set up default credential with 'gcloud auth application-default login'
mvn test -Pintegration-test
```

## Dockerized a Spring Boot application

```bash
# Under app dir
mvn install

# Under eventgenerator dir
export PUB_IMAGE_NAME=<your image, eg. gcr.io/my-project/spring-boot-jib>
mvn compile com.google.cloud.tools:jib-maven-plugin:3.3.1:build

# Under metrics dir
export SUB_IMAGE_NAME=<your image, eg. gcr.io/my-project/spring-boot-jib>
mvn compile com.google.cloud.tools:jib-maven-plugin:3.3.1:build [-P <profile>]
```

| Parameter | Default  | Comment                            | 
|-----------|----------|------------------------------------|
| profile   | complete | Value can be ack, nack or complete | 

## Environment Variable

### EventGenerator

```bash
EVENT_TOPIC = <EventTopic topic name>
PUBLISHER_THREADS = <Publisher executor thread number, default 4>
PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES = <Outstanding message number, default 100>
REST_PORT = <Rest port, default 8001>
GOOGLE_CLOUD_LOCATION = <GKE deployment’s location>
EVENT_GENERATOR_THREADS = <Service api threa number, default 200>
EVENT_GENERATOR_SLEEP_TIME = <Servie api sleep time in second, default 0.2>
EVENT_GENERATOR_RUNTIME = <Servie api run time in minute, default 5>
PUBLISHER_RETRY_INITIAL_TIMEOUT = <Publisher retry initial rpc timeout in second, default 5>
PUBLISHER_RETRY_TOTAL_TIMEOUT = <Publisher retry total timeout in second, default 600>
PUBLISHER_BATCH_SIZE = <Batch message, default 1>
```

### Metrics

```bash
EVENT_SUBSCRIPTION = <EventSubscription subscription name>
SUBSCRIBER_PARALLEL_PULL_COUNT = <Parallel pull number, default 1>
SUBSCRIBER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES = <Outstanding message number, default 100>
SUBSCRIBER_THREADS = <Subscriber executor thread number, default 4>
METRICS_TOPIC = <MetricTopic topic name>
PUBLISHER_THREADS = <Publisher executor thread number, default 4>
PUBLISHER_BATCH_SIZE = <Batch message, default 100>
```

## Api

### Publisher

#### 1. Publish a random generate message

| Parameter     | Type  | Default | Comment                                                    |
|---------------|-------|---------|------------------------------------------------------------|
| times         | int   | -1      | number of msg each thread publish (-1 means infinite loop) |
| thread        | int   | 1       | number of thread                                           |
| sleep         | float | 1       | time to sleep after each message (in second)               |
| executionTime | float | -1      | time to execute the task (in minute) (-1 means no limit)   |

ExecutionTime works when times is in infinite mode (times = -1)

```bash
[POST] api/msg/random?times=30&thread=2
```

#### 2. Shutdown msg publishing

```bash
[POST] api/msg/shutdown
```
