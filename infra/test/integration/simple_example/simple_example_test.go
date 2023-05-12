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

	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/gcloud"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/golden"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/tft"
	"github.com/stretchr/testify/assert"
	"context"
	"flag"
	"path/filepath"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/api/core/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
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

		// Get all publisher and subscriber cluster credentials
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", euPubClusterName, euPubClusterLocation), gcloudArgs)
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", usPubClusterName, usPubClusterLocation), gcloudArgs)
		gcloud.Run(t, fmt.Sprintf("container clusters get-credentials %s --region %s --format=json", usSubClusterName, usSubClusterLocation), gcloudArgs)

		// Generate Kubernetes config
		kubeconfig := GetKubeconfg()

		// Concat string to get clusterContextName, containerName and configMapName
		euPubClusterContextName := strings.Join([]string{"gke", projectID, euPubClusterLocation, euPubClusterName}, "_")
		euPubContainerName := strings.Join([]string{projectID, "publisher", euPubClusterLocation}, "-")
		usPubClusterContextName := strings.Join([]string{"gke", projectID, usPubClusterLocation, usPubClusterName}, "_")
		usPubContainerName := strings.Join([]string{projectID, "publisher", usPubClusterLocation}, "-")
		usSubClusterContextName := strings.Join([]string{"gke", projectID, usSubClusterLocation, usSubClusterName}, "_")
		usSubClusterConfigMapName := strings.Join([]string{projectID, "subscriber-config-maps", usSubClusterLocation}, "-")
		usSubContainerName := strings.Join([]string{projectID, "subscriber", usSubClusterLocation}, "-")

		imageHomeUrl :=	"gcr.io/aemon-projects-dev-000/"
		publisherImageNameTag := "jss-psi-java-event-generator:latest"
		subscriberImageNameTag := "jss-psi-java-metrics-ack:latest"

		// Printf all clusterContextName
		fmt.Printf("euPubClusterContextName: %s\n", euPubClusterContextName)
		fmt.Printf("usPubClusterContextName: %s\n", usPubClusterContextName)
		fmt.Printf("usSubClusterContextName: %s\n", usSubClusterContextName)

		// Get the clientset for the clusters
		euPubClientset, err := SwitchContextAndGetClientset(euPubClusterContextName, *kubeconfig)
		assert.NoError(err)
		usPubClientset, err := SwitchContextAndGetClientset(usPubClusterContextName, *kubeconfig)
		assert.NoError(err)
		usSubClientset, err := SwitchContextAndGetClientset(usSubClusterContextName, *kubeconfig)
		assert.NoError(err)

		// Get EU region publishers GKE cluster pods using euPubClusterNamespace
		euPubClusterPods, err := euPubClientset.CoreV1().Pods(euPubClusterNamespace).List(context.TODO(), metav1.ListOptions{})
		assert.NoError(err)

		fmt.Printf("There are %d pods in the %s namespace\n", len(euPubClusterPods.Items), euPubClusterNamespace)
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
					}
					assert.Equal(true, findEnvGoogleCloudLocationResult, "expected to find GOOGLE_CLOUD_LOCATION envVar")

					// Assert container image
					euPubExpectedImage := imageHomeUrl + publisherImageNameTag
					assert.Equal(container.Image, euPubExpectedImage, "expected container image to be " + euPubExpectedImage)
				}
			}
		}

		// Get US region publishers GKE cluster pods using usPubClusterNamespace
		usPubClusterPods, err := usPubClientset.CoreV1().Pods(usPubClusterNamespace).List(context.TODO(), metav1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("There are %d pods in the %s namespace\n", len(usPubClusterPods.Items), usPubClusterNamespace)
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
					}
					assert.Equal(true, findEnvGoogleCloudLocationResult, "expected to find GOOGLE_CLOUD_LOCATION envVar")

					// Assert container image
					usPubExpectedImage := imageHomeUrl + publisherImageNameTag
					assert.Equal(container.Image, usPubExpectedImage, "expected container image to be " + usPubExpectedImage)
				}
			}
		}

		// Get US region subscribers GKE cluster pods using usSubClusterNamespace
		usSubClusterPods, err := usSubClientset.CoreV1().Pods(usSubClusterNamespace).List(context.TODO(), metav1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("There are %d pods in the %s namespace\n", len(usSubClusterPods.Items), usSubClusterNamespace)
		// Get usSubCluster ConfigMap contents
		configMaps, err := usSubClientset.CoreV1().ConfigMaps("").List(context.TODO(), metav1.ListOptions{})
		assert.NoError(err)
		fmt.Printf("usSubCluster configMapData:\n")
		var usSubCm v1.ConfigMap
		for i, cm := range configMaps.Items {
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
					usSubExpectedImage := imageHomeUrl + subscriberImageNameTag
					assert.Equal(container.Image, usSubExpectedImage, "expected container image to be " + usSubExpectedImage)
				}
			}
		}
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
func GetKubeconfg() (*string) {
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()
	return kubeconfig
}

// Switch kubernetes context and return clientset
func SwitchContextAndGetClientset(contextName, kubeconfig string) (*kubernetes.Clientset, error) {
	config := LoadConfigFromPath(kubeconfig)
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

	buildConfig, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		return nil, err
	}
	clientset, err := kubernetes.NewForConfig(buildConfig)
	return clientset, err
}

func LoadConfigFromPath(kubeconfig string) clientcmd.ClientConfig {
	rules := clientcmd.NewDefaultClientConfigLoadingRules()
	rules.ExplicitPath = kubeconfig
	return clientcmd.NewNonInteractiveDeferredLoadingClientConfig(rules, &clientcmd.ConfigOverrides{})
}

func PrintEnvContent(envName string, envValue string) {
	fmt.Printf("Env Name: %s, Value: %s\n", envName, envValue)
}
