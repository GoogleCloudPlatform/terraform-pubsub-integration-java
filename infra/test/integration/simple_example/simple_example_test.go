// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package simple_example

import (
	"fmt"
	"testing"
	"strings"
	"strconv"
	"errors"
	"time"
	"io/ioutil"

	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/gcloud"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/golden"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/tft"
	"github.com/parnurzeal/gorequest"
	"github.com/stretchr/testify/assert"
	"context"
	"flag"
	"path/filepath"
	metaV1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/api/core/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
	"k8s.io/apimachinery/pkg/util/wait"
	"cloud.google.com/go/bigquery"
	"google.golang.org/api/iterator"
	"cloud.google.com/go/pubsub"
)

func TestSimpleExample(t *testing.T) {
	example := tft.NewTFBlueprintTest(t)

	example.DefineVerify(func(assert *assert.Assertions) {
		projectID := example.GetTFSetupStringOutput("project_id")
		gcloudArgs := gcloud.WithCommonArgs([]string{"--project", projectID})

		// Check if the ErrorTopic exists after services are deployed
		errorTopicName := example.GetStringOutput("errors_topic_name")
		errorTopic := gcloud.Run(t, fmt.Sprintf("pubsub topics describe %s --format=json", errorTopicName), gcloudArgs)
		assert.NotEmpty(errorTopic)

		// Check if the MetricsTopic exists after services are deployed
		metricsTopicName := example.GetStringOutput("metrics_topic_name")
		metricsTopic := gcloud.Run(t, fmt.Sprintf("pubsub topics describe %s --format=json", metricsTopicName), gcloudArgs)
		assert.NotEmpty(metricsTopic)
		// Check MetricsTopic config
		metricsTopicSchemaName := metricsTopic.Get("schemaSettings").Get("schema").String()
		metricsTopicSchema := gcloud.Run(t, fmt.Sprintf("pubsub schemas describe %s --format=json", metricsTopicSchemaName), gcloudArgs)
		assert.NotEmpty(metricsTopicSchema)
		assert.Equal("AVRO", metricsTopicSchema.Get("type").String(), "expected avro schema is valid")

		// Check if the EventTopic exists
		eventTopicName := example.GetStringOutput("event_topic_name")
		eventTopic := gcloud.Run(t, fmt.Sprintf("pubsub topics describe %s --format=json", eventTopicName), gcloudArgs)
		assert.NotEmpty(eventTopic)
		// Check EventTopic config
		eventTopicSchemaName := eventTopic.Get("schemaSettings").Get("schema").String()
		eventTopicSchema := gcloud.Run(t, fmt.Sprintf("pubsub schemas describe %s --format=json", eventTopicSchemaName), gcloudArgs)
		assert.NotEmpty(eventTopicSchema)
		assert.Equal("AVRO", eventTopicSchema.Get("type").String(), "expected avro schema is valid")

		// Check event subscription config
		eventSubscriptionName := example.GetStringOutput("event_subscription_name")
		eventSubscription := gcloud.Run(t, fmt.Sprintf("pubsub subscriptions describe %s --format=json", eventSubscriptionName), gcloudArgs)
		assert.NotEmpty(eventSubscription)
		assert.Equal("ACTIVE", eventSubscription.Get("state").String(), "expected event subscriptions to be active")
		assert.True(eventSubscription.Get("enableExactlyOnceDelivery").Bool())
		assert.Equal(errorTopic.Get("name").String(), eventSubscription.Get("deadLetterPolicy").Get("deadLetterTopic").String())

		// Check metrics subscription config
		metricsSubscriptionName := example.GetStringOutput("metrics_subscription_name")
		metricsSubscription := gcloud.Run(t, fmt.Sprintf("pubsub subscriptions describe %s --format=json", metricsSubscriptionName), gcloudArgs)
		assert.NotEmpty(metricsSubscription)
		assert.Equal("ACTIVE", metricsSubscription.Get("state").String(), "expected metrics subscriptions to be active")
		if assert.NotEmpty(metricsSubscription.Get("bigqueryConfig").Get("writeMetadata")) {
			assert.False(metricsSubscription.Get("bigqueryConfig").Get("writeMetadata").Bool())
		}
		assert.NotEmpty(metricsSubscription.Get("bigqueryConfig").Get("table"))

		// For golden projectID sanitizer
		gcloud.Run(t, fmt.Sprintf("config set project %s", projectID))

		// Check publisher deployment configs and status deployed in EU region
		euPubClusterName := example.GetStringOutput("europe_north1_publisher_cluster_name")
		euPubClusterNamespace := example.GetStringOutput("europe_north1_publisher_cluster_namespace")
		euPubClusterLocation := "europe-north1"
		ClusterStatusAndConfigCheck(t, assert, euPubClusterName, euPubClusterLocation, projectID)

		// Check publisher deployment configs and status deployed in US region
		usPubClusterName := example.GetStringOutput("us_west1_publisher_cluster_name")
		usPubClusterNamespace := example.GetStringOutput("us_west1_publisher_cluster_namespace")
		usPubClusterLocation := "us-west1"
		ClusterStatusAndConfigCheck(t, assert, usPubClusterName, usPubClusterLocation, projectID)

		// Check subscriber deployment configs and status deployed in the US region
		usSubClusterName := example.GetStringOutput("us_west1_subscriber_cluster_name")
		usSubClusterNamespace := example.GetStringOutput("us_west1_subscriber_cluster_namespace")
		usSubClusterLocation := "us-west1"
		ClusterStatusAndConfigCheck(t, assert, usSubClusterName, usSubClusterLocation, projectID)

		// List forwarding-rules
		fmt.Printf("Listing forwarding-rules using gcloud\n")
		externalLoadBalancers := gcloud.Run(t, ("compute forwarding-rules list --format=json"), gcloudArgs).Array()

		// Stop publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StopPublisher(t, assert, externalLoadBalancerIPAddress)
		}

		// Get all publisher and subscriber cluster credentials
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", euPubClusterName, euPubClusterLocation), gcloudArgs)
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", usPubClusterName, usPubClusterLocation), gcloudArgs)
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", usSubClusterName, usSubClusterLocation), gcloudArgs)

		// Generate Kubernetes config
		kubeConfig := GetKubeConfg()

		// Concat string to get clusterContextName, containerName, configMapName and deploymentName
		euPubClusterContextName := strings.Join([]string{"gke", projectID, euPubClusterLocation, euPubClusterName}, "_")
		usPubClusterContextName := strings.Join([]string{"gke", projectID, usPubClusterLocation, usPubClusterName}, "_")
		usSubClusterContextName := strings.Join([]string{"gke", projectID, usSubClusterLocation, usSubClusterName}, "_")

		euPubContainerName := strings.Join([]string{projectID, "publisher", euPubClusterLocation}, "-")
		usPubContainerName := strings.Join([]string{projectID, "publisher", usPubClusterLocation}, "-")
		usSubContainerName := strings.Join([]string{projectID, "subscriber", usSubClusterLocation}, "-")

		euPubClusterConfigMapName := strings.Join([]string{projectID, "publisher-config-maps", euPubClusterLocation}, "-")
		usPubClusterConfigMapName := strings.Join([]string{projectID, "publisher-config-maps", usPubClusterLocation}, "-")
		usSubClusterConfigMapName := strings.Join([]string{projectID, "subscriber-config-maps", usSubClusterLocation}, "-")

		usSubClusterDeploymentName := strings.Join([]string{projectID, "subscriber-deployment", usSubClusterLocation}, "-")

		imageHomeUrl :=	"gcr.io/aemon-projects-dev-000/"
		publisherImageNameTag := "jss-psi-java-event-generator:latest"
		subscriberAckImageNameTag := "jss-psi-java-metrics-ack:latest"
		subscriberNackImageNameTag := "jss-psi-java-metrics-nack:latest"
		subscriberCompleteImageNameTag := "jss-psi-java-metrics-complete:latest"

		// Printf all clusterContextName
		fmt.Printf("euPubClusterContextName: %s\n", euPubClusterContextName)
		fmt.Printf("usPubClusterContextName: %s\n", usPubClusterContextName)
		fmt.Printf("usSubClusterContextName: %s\n", usSubClusterContextName)

		// Get the clientset for the clusters
		euPubClientset, err := SwitchContextAndGetClientset(euPubClusterContextName, *kubeConfig)
		assert.NoError(err)
		usPubClientset, err := SwitchContextAndGetClientset(usPubClusterContextName, *kubeConfig)
		assert.NoError(err)
		usSubClientset, err := SwitchContextAndGetClientset(usSubClusterContextName, *kubeConfig)
		assert.NoError(err)

		// Get EU region publishers GKE cluster pods using euPubClusterNamespace
		euPubClusterPods, err := euPubClientset.CoreV1().Pods(euPubClusterNamespace).List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("There are %d pods in the %s namespace\n", len(euPubClusterPods.Items), euPubClusterNamespace)

		// Start Publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StartPublisher(t, assert, externalLoadBalancerIPAddress, 4, 5.0)
		}

		// Get euPubCluster ConfigMap contents
		euPubConfigMaps, err := euPubClientset.CoreV1().ConfigMaps("").List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("euPubCluster configMapData:\n")
		var euPubCm v1.ConfigMap
		for i, cm := range euPubConfigMaps.Items {
			if cm.GetName() == euPubClusterConfigMapName {
				fmt.Printf("[%d] %s\n", i, cm.GetName())
				fmt.Printf("[%d] %s\n", i, cm.Data)
				for k, v := range cm.Data {
					fmt.Printf("%s: %s\n", k, v)
				}
				euPubCm = cm
			}
		}

		for _, pod := range euPubClusterPods.Items {
			// Assert Pod status
			fmt.Printf("Pod name: %s\n", pod.ObjectMeta.Name)
			assert.Equal("Running", string(pod.Status.Phase), "expected pod to be active")

			// Find the container with the specified name
			for _, container := range pod.Spec.Containers {
				if container.Name == euPubContainerName {
					fmt.Printf("Container name: %s\n", container.Name)

					// Assert container environment variables
					var findEnvGoogleCloudLocationResult bool = false
					for _, envVar := range container.Env {
						// Assert GOOGLE_CLOUD_LOCATION
						if envVar.Name == "GOOGLE_CLOUD_LOCATION" {
							assert.Equal("europe-north1", envVar.Value, "expected GOOGLE_CLOUD_LOCATION to be europe-north1")
							findEnvGoogleCloudLocationResult = true
						}

						// Assert PUBLISHER_BATCH_SIZE
						if envVar.Name == "PUBLISHER_BATCH_SIZE" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = euPubCm.Data["PUBLISHER_BATCH_SIZE"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_BATCH_SIZE not to be empty")
							assert.Equal(1, envIntVar, "expected PUBLISHER_BATCH_SIZE to be 1")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_RETRY_TOTAL_TIMEOUT
						if envVar.Name == "PUBLISHER_RETRY_TOTAL_TIMEOUT" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = euPubCm.Data["PUBLISHER_RETRY_TOTAL_TIMEOUT"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_RETRY_TOTAL_TIMEOUT not to be empty")
							assert.Equal(600, envIntVar, "expected PUBLISHER_RETRY_TOTAL_TIMEOUT to be 600")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_RETRY_INITIAL_TIMEOUT
						if envVar.Name == "PUBLISHER_RETRY_INITIAL_TIMEOUT" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = euPubCm.Data["PUBLISHER_RETRY_INITIAL_TIMEOUT"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_RETRY_INITIAL_TIMEOUT not to be empty")
							assert.Equal(5, envIntVar, "expected PUBLISHER_RETRY_INITIAL_TIMEOUT to be 5")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES
						if envVar.Name == "PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = euPubCm.Data["PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES not to be empty")
							assert.Equal(100, envIntVar, "expected PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES to be 100")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}
					}
					assert.Equal(true, findEnvGoogleCloudLocationResult, "expected to find GOOGLE_CLOUD_LOCATION envVar")

					// Assert container image
					euPubExpectedImage := imageHomeUrl + publisherImageNameTag
					assert.Equal(container.Image, euPubExpectedImage, "expected container image to be " + euPubExpectedImage)
				}
			}
		}

		// Get US region publishers GKE cluster pods using usPubClusterNamespace
		usPubClusterPods, err := usPubClientset.CoreV1().Pods(usPubClusterNamespace).List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("There are %d pods in the %s namespace\n", len(usPubClusterPods.Items), usPubClusterNamespace)
		// Get usPubCluster ConfigMap contents
		usPubConfigMaps, err := usPubClientset.CoreV1().ConfigMaps("").List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("usPubCluster configMapData:\n")
		var usPubCm v1.ConfigMap
		for i, cm := range usPubConfigMaps.Items {
			if cm.GetName() == usPubClusterConfigMapName {
				fmt.Printf("[%d] %s\n", i, cm.GetName())
				fmt.Printf("[%d] %s\n", i, cm.Data)
				for k, v := range cm.Data {
					fmt.Printf("%s: %s\n", k, v)
				}
				usPubCm = cm
			}
		}

		for _, pod := range usPubClusterPods.Items {
			// Assert Pod status
			fmt.Printf("Pod name: %s\n", pod.ObjectMeta.Name)
			assert.Equal("Running", string(pod.Status.Phase), "expected pod to be active")

			// Find the container with the specified name
			for _, container := range pod.Spec.Containers {
				if container.Name == usPubContainerName {
					fmt.Printf("Container name: %s\n", container.Name)

					// Assert container environment variables
					var findEnvGoogleCloudLocationResult bool = false
					for _, envVar := range container.Env {
						// Assert GOOGLE_CLOUD_LOCATION
						if envVar.Name == "GOOGLE_CLOUD_LOCATION" {
							assert.Equal("us-west1", envVar.Value, "expected GOOGLE_CLOUD_LOCATION to be us-west1")
							findEnvGoogleCloudLocationResult = true
						}

						// Assert PUBLISHER_BATCH_SIZE
						if envVar.Name == "PUBLISHER_BATCH_SIZE" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usPubCm.Data["PUBLISHER_BATCH_SIZE"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_BATCH_SIZE not to be empty")
							assert.Equal(1, envIntVar, "expected PUBLISHER_BATCH_SIZE to be 1")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_RETRY_TOTAL_TIMEOUT
						if envVar.Name == "PUBLISHER_RETRY_TOTAL_TIMEOUT" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usPubCm.Data["PUBLISHER_RETRY_TOTAL_TIMEOUT"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_RETRY_TOTAL_TIMEOUT not to be empty")
							assert.Equal(600, envIntVar, "expected PUBLISHER_RETRY_TOTAL_TIMEOUT to be 600")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_RETRY_INITIAL_TIMEOUT
						if envVar.Name == "PUBLISHER_RETRY_INITIAL_TIMEOUT" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usPubCm.Data["PUBLISHER_RETRY_INITIAL_TIMEOUT"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_RETRY_INITIAL_TIMEOUT not to be empty")
							assert.Equal(5, envIntVar, "expected PUBLISHER_RETRY_INITIAL_TIMEOUT to be 5")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES
						if envVar.Name == "PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = euPubCm.Data["PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES not to be empty")
							assert.Equal(100, envIntVar, "expected PUBLISHER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES to be 100")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}
					}
					assert.Equal(true, findEnvGoogleCloudLocationResult, "expected to find GOOGLE_CLOUD_LOCATION envVar")

					// Assert container image
					usPubExpectedImage := imageHomeUrl + publisherImageNameTag
					assert.Equal(container.Image, usPubExpectedImage, "expected container image to be " + usPubExpectedImage)
				}
			}
		}

		// Get US region subscribers GKE cluster pods using usSubClusterNamespace
		usSubClusterPods, err := usSubClientset.CoreV1().Pods(usSubClusterNamespace).List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("There are %d pods in the %s namespace\n", len(usSubClusterPods.Items), usSubClusterNamespace)
		// Get usSubCluster ConfigMap contents
		usSubConfigMaps, err := usSubClientset.CoreV1().ConfigMaps("").List(context.TODO(), metaV1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("usSubCluster configMapData:\n")
		var usSubCm v1.ConfigMap
		for i, cm := range usSubConfigMaps.Items {
			if cm.GetName() == usSubClusterConfigMapName {
				fmt.Printf("[%d] %s\n", i, cm.GetName())
				fmt.Printf("[%d] %s\n", i, cm.Data)
				for k, v := range cm.Data {
					fmt.Printf("%s: %s\n", k, v)
				}
				usSubCm = cm
			}
		}

		for _, pod := range usSubClusterPods.Items {
			// Assert Pod status
			fmt.Printf("Pod name: %s\n", pod.ObjectMeta.Name)
			assert.Equal("Running", string(pod.Status.Phase), "expected pod to be active")

			// Find the container with the specified name
			for _, container := range pod.Spec.Containers {
				if container.Name == usSubContainerName {
					fmt.Printf("Container name: %s\n", container.Name)
					fmt.Printf("Container image: %s\n", container.Image)

					// Assert container environment variables
					var validateConcurrencyOn bool = false

					for _, envVar := range container.Env {
						// Assert publisher batching is on
						if envVar.Name == "PUBLISHER_BATCH_SIZE" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usSubCm.Data["PUBLISHER_BATCH_SIZE"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected PUBLISHER_BATCH_SIZE not to be empty")
							assert.Greater(envIntVar, 1, "expected PUBLISHER_BATCH_SIZE to be greater than 1")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}

						// Assert subscriber concurrency is on
						if envVar.Name == "SUBSCRIBER_PARALLEL_PULL_COUNT" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usSubCm.Data["SUBSCRIBER_PARALLEL_PULL_COUNT"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected SUBSCRIBER_PARALLEL_PULL_COUNT not to be empty")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
							if envIntVar > 1 {
								validateConcurrencyOn = true
							}
						}
						if envVar.Name == "SUBSCRIBER_THREADS" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usSubCm.Data["SUBSCRIBER_THREADS"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected SUBSCRIBER_THREADS not to be empty")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
							if envIntVar > 1 {
								validateConcurrencyOn = true
							}
						}

						// Assert subscriber flow control is on
						if envVar.Name == "SUBSCRIBER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES" {
							var checkingValue = envVar.Value
							if checkingValue == "" {
								checkingValue = usSubCm.Data["SUBSCRIBER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES"]
							}
							envIntVar, err := strconv.Atoi(checkingValue)
							assert.NoError(err)
							assert.NotEmpty(envIntVar, "expected SUBSCRIBER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES not to be empty")
							assert.Greater(envIntVar, 1, "expected SUBSCRIBER_FLOW_CONTROL_MAX_OUTSTANDING_MESSAGES to be greater than 1")
							PrintEnvContent(envVar.Name, strconv.Itoa(envIntVar))
						}
					}
					assert.Equal(true, validateConcurrencyOn, "expected concurrency to be on")

					// Assert container image
					usSubExpectedImage := imageHomeUrl + subscriberAckImageNameTag
					assert.Equal(container.Image, usSubExpectedImage, "expected container image to be " + usSubExpectedImage)
				}
			}
		}

		// Restart publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			RestartPublisher(t, assert, externalLoadBalancerIPAddress, 5, 0.5)
		}

		// Check BigQuery table data
		fmt.Printf("Checking BigQuery table data\n")
		bqTableId := example.GetStringOutput("bq_table_id")
		// query interval is 3 minutes
		queryTimeDuration := 3 * time.Minute
		ackStart := time.Now().In(time.UTC)
		ackStartTime := ackStart.Format("2006-01-02 15:04:05")
		ackEndTime := ackStart.Add(queryTimeDuration).Format("2006-01-02 15:04:05")
		// Wait for 3 minutes
		fmt.Printf("Waiting for 3 minutes, from %s to %s\n", ackStartTime, ackEndTime)
		time.Sleep(queryTimeDuration)
		// Query BigQuery table
		ackDataCount, err := CountQueryAckFromBigquery(projectID, bqTableId, ackStartTime, ackEndTime)
		assert.NoError(err)
		fmt.Printf("BigQuery result: %d\n", ackDataCount)
		assert.Greater(ackDataCount, int64(0), "expected dataCount to be greater than 0 when image is " + subscriberAckImageNameTag)

		// Stop publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StopPublisher(t, assert, externalLoadBalancerIPAddress)
		}

		// Update subscriber Deployment's image to Nack
		fmt.Printf("Updating Deployment's image to %s\n", imageHomeUrl + subscriberNackImageNameTag)
		err = UpdateDeploymentImage(usSubClientset, usSubClusterNamespace, usSubClusterDeploymentName, imageHomeUrl + subscriberNackImageNameTag)
		assert.NoError(err)
		// Wait for Deployment to be updated
		fmt.Printf("Waiting for Deployment to be updated\n")
		var deploymentTimeout = 5 * time.Minute
		err = WaitForDeploymentToBeUpdated(usSubClientset, usSubClusterNamespace, usSubClusterDeploymentName, deploymentTimeout)
		assert.NoError(err)

		// Start publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StartPublisher(t, assert, externalLoadBalancerIPAddress, 5, 0.5)
		}

		// Check BigQuery table data
		fmt.Printf("Checking BigQuery table data\n")
		nackStart := time.Now().In(time.UTC)
		nackStartTime := nackStart.Format("2006-01-02 15:04:05")
		nackEndTime := nackStart.Add(queryTimeDuration).Format("2006-01-02 15:04:05")
		// Wait for 3 minutes
		fmt.Printf("Waiting for 3 minutes, from %s to %s\n", nackStartTime, nackEndTime)
		time.Sleep(queryTimeDuration)
		// Query BigQuery table
		nackDataCount, err := CountQueryAckFromBigquery(projectID, bqTableId, nackStartTime, nackEndTime)
		assert.NoError(err)
		fmt.Printf("BigQuery result: %d\n", nackDataCount)
		assert.Equal(nackDataCount, int64(0), "expected dataCount is 0 when image is " + subscriberNackImageNameTag)

		// Stop publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StopPublisher(t, assert, externalLoadBalancerIPAddress)
		}

		// Create pubsub metrics schema revision using complete avro schema file
		fmt.Printf("Creating pubsub metrics schema revision using complete avro schema file\n")
		metricsSchemaName := example.GetStringOutput("metrics_schema_name")
		schemaClient, err := pubsub.NewSchemaClient(context.Background(), projectID)
		assert.NoError(err)
		// Read an Avro schema file formatted in JSON
		avscSource, err := ioutil.ReadFile("../../../config/avro/MetricsComplete.avsc")
		assert.NoError(err)
		avroSourceStr := strings.Replace(string(avscSource), "MetricsComplete", "MetricsAck", -1)
		// Create a schema revision
		schemaConfig := pubsub.SchemaConfig {
			Name: 		fmt.Sprintf("projects/%s/schemas/%s", projectID, metricsSchemaName),
			Type: 		pubsub.SchemaAvro,
			Definition: avroSourceStr,
		}
		schema, err := schemaClient.CommitSchema(context.Background(), metricsSchemaName, schemaConfig)
		assert.NoError(err)
		assert.NotEmpty(schema)

		// Update subscriber Deployment's image to Complete
		fmt.Printf("Updating Deployment's image to %s\n", imageHomeUrl + subscriberCompleteImageNameTag)
		err = UpdateDeploymentImage(usSubClientset, usSubClusterNamespace, usSubClusterDeploymentName, imageHomeUrl + subscriberCompleteImageNameTag)
		assert.NoError(err)
		// Wait for Deployment to be updated
		fmt.Printf("Waiting for Deployment to be updated\n")
		err = WaitForDeploymentToBeUpdated(usSubClientset, usSubClusterNamespace, usSubClusterDeploymentName, deploymentTimeout)
		assert.NoError(err)

		// Start publisher tasks
		for _, externalLoadBalancer := range externalLoadBalancers {
			externalLoadBalancerIPAddress := externalLoadBalancer.Get("IPAddress").String()
			StartPublisher(t, assert, externalLoadBalancerIPAddress, 5, 0.5)
		}

		// Check BigQuery table data
		fmt.Printf("Checking BigQuery table data\n")
		completeStart := time.Now().In(time.UTC)
		completeStartTime := completeStart.Format("2006-01-02 15:04:05")
		completeEndTime := completeStart.Add(queryTimeDuration).Format("2006-01-02 15:04:05")
		// Wait for 3 minutes
		fmt.Printf("Waiting for 3 minutes, from %s to %s\n", completeStartTime, completeEndTime)
		time.Sleep(queryTimeDuration)
		// Query BigQuery table
		completeDataCount, err := CountQueryCompleteFromBigquery(projectID, bqTableId, completeStartTime, completeEndTime)
		assert.NoError(err)
		fmt.Printf("BigQuery result: %d\n", completeDataCount)
		assert.Greater(ackDataCount, int64(0), "expected dataCount to be greater than 0 when image is " + subscriberCompleteImageNameTag)
	})

	example.Test()
}

// Check Kubernetes cluster status and configs using golden file
func ClusterStatusAndConfigCheck(t *testing.T, assert *assert.Assertions, clusterName string, clusterLocation string, projectID string) {
	op := gcloud.Runf(t, "container clusters describe %s --zone %s --project %s", clusterName, clusterLocation, projectID)
	g := golden.NewOrUpdate(t, op.String(),
		golden.WithSanitizer(golden.StringSanitizer(clusterName, "CLUSTER_NAME")),
		golden.WithSanitizer(golden.StringSanitizer(clusterLocation, "CLUSTER_LOCATION")),
	)
	validateJSONPaths := []string{
		"name",
		"location",
		"autopilot.enabled",
	}
	for _, pth := range validateJSONPaths {
		g.JSONEq(assert, op, pth)
	}
	assert.Contains([]string{"RUNNING", "RECONCILING"}, op.Get("status").String())
}

// Get k8s config
func GetKubeConfg() (*string) {
	var kubeConfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeConfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeConfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()
	return kubeConfig
}

// Switch kubernetes context and return clientset
func SwitchContextAndGetClientset(contextName, kubeConfig string) (*kubernetes.Clientset, error) {
	config := LoadConfigFromPath(kubeConfig)
	rawConfig, err := config.RawConfig()
	if err != nil {
		return nil, err
	}
	if rawConfig.Contexts[contextName] == nil {
		return nil, errors.New("context not found")
	}
	rawConfig.CurrentContext = contextName
	err = clientcmd.ModifyConfig(clientcmd.NewDefaultPathOptions(), rawConfig, true)
	if err != nil {
		return nil, err
	}

	buildConfig, err := clientcmd.BuildConfigFromFlags("", kubeConfig)
	if err != nil {
		return nil, err
	}
	clientset, err := kubernetes.NewForConfig(buildConfig)
	if err != nil {
		return nil, err
	}
	return clientset, err
}

func LoadConfigFromPath(kubeConfig string) clientcmd.ClientConfig {
	rules := clientcmd.NewDefaultClientConfigLoadingRules()
	rules.ExplicitPath = kubeConfig
	return clientcmd.NewNonInteractiveDeferredLoadingClientConfig(rules, &clientcmd.ConfigOverrides{})
}

func PrintEnvContent(envName string, envValue string) {
	fmt.Printf("Env Name: %s, Value: %s\n", envName, envValue)
}

// Update Deployment's image
func UpdateDeploymentImage(clientset *kubernetes.Clientset, clusterNamespace string, deploymentName string, image string) (error) {
	deployment, err := clientset.AppsV1().Deployments(clusterNamespace).Get(context.Background(), deploymentName, metaV1.GetOptions{})
	if err != nil {
		return err
	}
	deployment.Spec.Template.Spec.Containers[0].Image = image
	deployment, err = clientset.AppsV1().Deployments(clusterNamespace).Update(context.Background(), deployment, metaV1.UpdateOptions{})
	return err
}

// WaitForDeploymentToBeUpdated waits for the deployment to be updated
func WaitForDeploymentToBeUpdated(clientset *kubernetes.Clientset, namespace string, deploymentName string, timeout time.Duration) error {
	// Sleep 10 seconds for waiting revision to be created
	time.Sleep(10 * time.Second)
	return wait.PollImmediate(1 * time.Second, timeout, func() (bool, error) {
		deployment, err := clientset.AppsV1().Deployments(namespace).Get(context.Background(), deploymentName, metaV1.GetOptions{})
		if err != nil {
			return false, err
		}
		// printf deplonment status
		fmt.Printf("Deployment status: %d/%d\n", deployment.Status.UpdatedReplicas, deployment.Status.Replicas)
		fmt.Printf("Deployment status value: %v\n", deployment.Status)

		if deployment.Status.UpdatedReplicas == deployment.Status.Replicas {
			return true, nil
		}
		return false, nil
	})
}

// Query ack data from BigQuery
func CountQueryAckFromBigquery(projectID string, bqTableId string, startTime string, endTime string) (int64, error) {
	client, err := bigquery.NewClient(context.Background(), projectID)
	if err != nil {
		return 0, err
	}
	defer client.Close()

	query := client.Query(
			`SELECT
				count(*) as dataCount
			FROM ` + bqTableId + `
			WHERE
				publish_timestamp >= '` + startTime + `' AND publish_timestamp < '` + endTime + `';`)

	rows, err := query.Read(context.Background())
	if err != nil {
		return 0, err
	}
	fmt.Println("BigQuery result:")
	for {
		var values []bigquery.Value
		err := rows.Next(&values)
		if err == iterator.Done {
			break
		}
		if err != nil {
			return 0, err
		}
		fmt.Println(values)
		return values[0].(int64), nil
	}
	return 0, errors.New("no data")
}

// Query complete data from BigQuery
func CountQueryCompleteFromBigquery(projectID string, bqTableId string, startTime string, endTime string) (int64, error) {
	client, err := bigquery.NewClient(context.Background(), projectID)
	if err != nil {
		return 0, err
	}
	defer client.Close()

	query := client.Query(
			`SELECT
				count(*) as dataCount
			FROM ` + bqTableId + `
			WHERE
				publish_timestamp >= '` + startTime + `' AND publish_timestamp < '` + endTime + `'
				AND battery_level_end is not null AND charged_total_kwh is not null;`)

	rows, err := query.Read(context.Background())
	if err != nil {
		return 0, err
	}
	fmt.Println("BigQuery result:")
	for {
		var values []bigquery.Value
		err := rows.Next(&values)
		if err == iterator.Done {
			break
		}
		if err != nil {
			return 0, err
		}
		fmt.Println(values)
		return values[0].(int64), nil
	}
	return 0, errors.New("no data")
}

// Restart Publihser
func RestartPublisher(t *testing.T, assert *assert.Assertions, externalLoadBalancerIPAddress string, threads int, runtime float64) {
	StopPublisher(t, assert, externalLoadBalancerIPAddress)
	StartPublisher(t, assert, externalLoadBalancerIPAddress, threads, runtime)
}

func StopPublisher(t *testing.T, assert *assert.Assertions, externalLoadBalancerIPAddress string) {
	// Send Http POST request to restart the publisher
	shutdownApiUrl := "http://" + externalLoadBalancerIPAddress + "/api/msg/shutdown"
	fmt.Printf("Sending Http POST request to : %s\n", shutdownApiUrl)
	request := gorequest.New().Timeout(20 * time.Second)
	_, _, errs := request.Post(shutdownApiUrl).End()
	for _, err := range errs {
		assert.NoError(err)
	}
}

func StartPublisher(t *testing.T, assert *assert.Assertions, externalLoadBalancerIPAddress string, threads int, runtime float64) {
	// payload: {threads int, runtime float}
	payload := fmt.Sprintf("{\"threads\": %d, \"runtime\": %f}", threads, runtime)
	startupApiUrl := "http://" + externalLoadBalancerIPAddress + "/api/msg/random?threads=" + strconv.Itoa(threads) + "&runtime=" + strconv.FormatFloat(runtime, 'f', 2, 64)
	fmt.Printf("Sending Http POST request to : %s, payload: %s\n", startupApiUrl, payload)
	request := gorequest.New().Timeout(20 * time.Second)
	_, _, errs := request.Post(startupApiUrl).End()
	for _, err := range errs {
		assert.NoError(err)
	}
}
