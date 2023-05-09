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

output "errors_topic_name" {
  description = "The name of the error topic"
  value       = module.simple.errors_topic_name
}

output "metrics_topic_name" {
  description = "The name of the metric topic"
  value       = module.simple.metrics_topic_name
}

output "event_topic_name" {
  description = "The name of the event topic"
  value       = module.simple.event_topic_name
}

output "event_subscription_name" {
  description = "The name of the event subscription created for Pub/Sub."
  value       = module.simple.event_subscription_name
}

output "metrics_subscription_name" {
  description = "The name of the metrics subscription created for Pub/Sub."
  value       = module.simple.metrics_subscription_name
}

output "europe_north1_publisher_cluster_name" {
  description = "The name of the GKE cluster in the europe-north1 region used by the publisher."
  value       = module.simple.europe_north1_publisher_cluster_name
}

output "us_west1_publisher_cluster_name" {
  description = "The name of the GKE cluster in the us-west1 region used by the publisher."
  value       = module.simple.us_west1_publisher_cluster_name
}

output "us_west1_subscriber_cluster_name" {
  description = "The name of the GKE cluster in the us-west1 region used by the publisher."
  value       = module.simple.us_west1_subscriber_cluster_name
}
