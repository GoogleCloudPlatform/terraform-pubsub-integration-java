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

	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/gcloud"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/golden"
	"github.com/GoogleCloudPlatform/cloud-foundation-toolkit/infra/blueprint-test/pkg/tft"
	"github.com/stretchr/testify/assert"
)

func TestSimpleExample(t *testing.T) {
	example := tft.NewTFBlueprintTest(t)

	example.DefineVerify(func(assert *assert.Assertions) {
		projectID := example.GetTFSetupStringOutput("project_id")
		gcloudArgs := gcloud.WithCommonArgs([]string{"--project", projectID})

		// Check if the ErrorTopic exists
		errorTopicName := example.GetStringOutput("errors_topic_name")
		errorTopic := gcloud.Run(t, fmt.Sprintf("pubsub topics describe %s --format=json", errorTopicName), gcloudArgs)
		assert.NotEmpty(errorTopic)

		// Check if the MetricsTopic exists
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

		// Check EU publisher deployment configs and status
		euPubClusterName := example.GetStringOutput("europe_north1_publisher_cluster_name")
		euPubClusterLocation := "europe-north1"
		ClusterStatusAndConfigCheck(t, assert, euPubClusterName, euPubClusterLocation, projectID)

		// Check US publisher deployment configs and status
		usPubClusterName := example.GetStringOutput("us_west1_publisher_cluster_name")
		usPubClusterLocation := "us-west1"
		ClusterStatusAndConfigCheck(t, assert, usPubClusterName, usPubClusterLocation, projectID)

		// Check US subscriber deployment configs and status
		usSubClusterName := example.GetStringOutput("us_west1_subscriber_cluster_name")
		usSubClusterLocation := "us-west1"
		ClusterStatusAndConfigCheck(t, assert, usSubClusterName, usSubClusterLocation, projectID)

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
