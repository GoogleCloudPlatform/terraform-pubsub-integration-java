/**
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

output "project_id" {
  description = "The ID of the project where resources are deployed to"
  value       = data.google_project.project.project_id
}

output "errors_topic_name" {
  description = "The name of the error topic"
  value       = google_pubsub_topic.errors.name
}

output "metrics_topic_name" {
  description = "The name of the metric topic"
  value       = google_pubsub_topic.metrics.name
}

output "event_topic_name" {
  description = "The name of the event topic"
  value       = google_pubsub_topic.event.name
}

output "event_subscription_name" {
  description = "The name of the event subscription created for Pub/Sub"
  value       = google_pubsub_subscription.event.name
}

output "metrics_subscription_name" {
  description = "The name of the metrics subscription created for Pub/Sub"
  value       = google_pubsub_subscription.metrics.name
}

output "eu_publisher_cluster_info" {
  description = "The cluster information for the publisher cluster in eu"
  value       = module.eu_publisher_cluster.cluster_info
}

output "us_publisher_cluster_info" {
  description = "The cluster information for the publisher cluster in us"
  value       = module.us_publisher_cluster.cluster_info
}

output "us_subscriber_cluster_info" {
  description = "The cluster information for the subscriber cluster in us"
  value       = module.us_subscriber_cluster.cluster_info
}

output "bq_table_id" {
  description = "The ID of the BigQuery table"
  value       = "${data.google_project.project.project_id}.${module.bigquery.dataset_id}.${module.bigquery.table_id}"
}

output "metrics_schema_name" {
  description = "The name of the metrics schema"
  value       = google_pubsub_schema.metrics.name
}
