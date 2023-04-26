module "project_services" {
  source                      = "terraform-google-modules/project-factory/google//modules/project_services"
  version                     = "~> 14.1"
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

data "google_project" "current" {
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
  depends_on = [
    module.project_services
  ]

  project                 = data.google_project.current.project_id
  name                    = "primary"
  auto_create_subnetworks = true
}

resource "google_pubsub_topic" "events" {
  depends_on = [
    module.project_services,
  ]

  name = "EventsTopic"
  schema_settings {
    schema   = "projects/${data.google_project.current.project_id}/schemas/${google_pubsub_schema.events.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_schema" "events" {
  depends_on = [
    module.project_services,
  ]

  name       = "evChargeEvent"
  type       = "AVRO"
  definition = file("${path.module}/../config/avro/evChargeEvent.avsc")
}

resource "google_pubsub_subscription" "events" {
  depends_on = [
    google_project_iam_member.pubsub
  ]

  name  = "EventSubscription"
  topic = google_pubsub_topic.events.name
  dead_letter_policy {
    dead_letter_topic     = google_pubsub_topic.errors.id
    max_delivery_attempts = 5
  }
  labels = var.labels
}

resource "google_pubsub_topic" "errors" {
  depends_on = [
    module.project_services,
    google_project_iam_member.pubsub
  ]

  name                       = "ErrorsTopic"
  message_retention_duration = "600s"
  schema_settings {
    schema   = "projects/${data.google_project.current.project_id}/schemas/${google_pubsub_schema.events.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_topic" "metrics" {
  depends_on = [
    module.project_services,
  ]

  name = "MetricsTopic"
  schema_settings {
    schema   = "projects/${data.google_project.current.project_id}/schemas/${google_pubsub_schema.metrics.name}"
    encoding = "JSON"
  }
  labels = var.labels
}

resource "google_pubsub_schema" "metrics" {
  depends_on = [
    module.project_services,
  ]

  name       = "evChargeMetric"
  type       = "AVRO"
  definition = file("${path.module}/../config/avro/evChargeMetricComplete.avsc")
}

resource "google_pubsub_subscription" "metrics" {
  depends_on = [
    google_project_iam_member.pubsub
  ]

  name  = "MetricsSubscription"
  topic = google_pubsub_topic.metrics.name
  bigquery_config {
    table            = "${data.google_project.current.project_id}.${module.bigquery.dataset_id}.${module.bigquery.table_id}"
    use_topic_schema = true
  }
  labels = var.labels
}

module "bigquery" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/bigquery"

  dataset_id = "ev_charging"
  table_id   = "charging_sessions"
  schema     = file("${path.module}/../config/avro/bigquery/evChargeMetricComplete.json")
  labels     = var.labels
}

resource "google_project_iam_member" "pubsub" {
  depends_on = [
    module.project_services
  ]

  for_each = toset([
    "roles/bigquery.metadataViewer",
    "roles/bigquery.dataEditor"
  ])

  project = data.google_project.current.project_id
  role    = each.key
  member  = "serviceAccount:service-${data.google_project.current.number}@gcp-sa-pubsub.iam.gserviceaccount.com"
}
