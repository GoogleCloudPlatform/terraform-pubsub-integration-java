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

module "project_services" {
  source                      = "terraform-google-modules/project-factory/google//modules/project_services"
  version                     = "~> 18.0"
  disable_services_on_destroy = false
  project_id                  = var.project_id

  activate_apis = [
    "compute.googleapis.com",
    "iam.googleapis.com",
    "serviceusage.googleapis.com",
    "cloudresourcemanager.googleapis.com",
    "cloudbuild.googleapis.com",
    "monitoring.googleapis.com",
    "container.googleapis.com",
    "pubsub.googleapis.com",
    "bigquery.googleapis.com",
  ]
}

data "google_project" "project" {
  depends_on = [
    module.project_services
  ]

  project_id = var.project_id
}

data "google_client_config" "default" {
  depends_on = [
    module.project_services
  ]
}

resource "google_compute_network" "primary" {
  name                    = "pubsub-integration-java"
  project                 = data.google_project.project.project_id
  auto_create_subnetworks = true
}

resource "google_pubsub_topic" "event" {
  name = "event-topic-pubsub-integration-java"
  schema_settings {
    schema   = "projects/${data.google_project.project.project_id}/schemas/${google_pubsub_schema.event.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_schema" "event" {
  depends_on = [
    module.project_services,
  ]

  name       = "event-pubsub-integration-java"
  type       = "AVRO"
  definition = file("${path.module}/config/avro/Event.avsc")
}

resource "google_pubsub_subscription" "event" {
  depends_on = [
    google_project_iam_member.pubsub
  ]

  name  = "event-subscription-pubsub-integration-java"
  topic = google_pubsub_topic.event.name
  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.errors.id
    max_delivery_attempts = 5
  }
  enable_exactly_once_delivery = true
  labels                       = var.labels
}

resource "google_pubsub_topic" "errors" {
  depends_on = [
    google_project_iam_member.pubsub
  ]

  name                       = "errors-topic-pubsub-integration-java"
  message_retention_duration = "600s"
  schema_settings {
    schema   = "projects/${data.google_project.project.project_id}/schemas/${google_pubsub_schema.event.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_topic" "metrics" {
  name = "metrics-topic-pubsub-integration-java"
  schema_settings {
    schema   = "projects/${data.google_project.project.project_id}/schemas/${google_pubsub_schema.metrics.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_schema" "metrics" {
  depends_on = [
    module.project_services,
  ]

  name       = "metrics-pubsub-integration-java"
  type       = "AVRO"
  definition = file("${path.module}/config/avro/MetricsAck.avsc")
}

resource "google_pubsub_subscription" "metrics" {
  depends_on = [
    google_project_iam_member.pubsub
  ]

  name  = "metrics-subscription-pubsub-integration-java"
  topic = google_pubsub_topic.metrics.name
  bigquery_config {
    table            = "${data.google_project.project.project_id}.${module.bigquery.dataset_id}.${module.bigquery.table_id}"
    use_topic_schema = true
  }
  labels = var.labels
}

module "bigquery" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/bigquery"

  dataset_id = "ev_charging_java"
  table_id   = "charging_sessions"
  schema     = file("${path.module}/config/avro/bigquery/MetricsComplete.json")
  labels     = var.labels
}

resource "google_project_iam_member" "pubsub" {
  for_each = toset([
    "roles/bigquery.metadataViewer",
    "roles/bigquery.dataEditor",
    "roles/pubsub.subscriber",
    "roles/pubsub.publisher",
  ])

  project = data.google_project.project.project_id
  role    = each.key
  member  = "serviceAccount:service-${data.google_project.project.number}@gcp-sa-pubsub.iam.gserviceaccount.com"
}
