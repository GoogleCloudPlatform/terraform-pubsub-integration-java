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

locals {
  us_publisher_namespace                = "${var.region}-publisher"
  us_publisher_k8s_service_account_name = "${var.region}-publisher"
  us_publisher_base_entries = [
    {
      name  = "namespace"
      value = local.us_publisher_namespace
    },
    {
      name  = "gcp_service_account_email"
      value = module.us_publisher_cluster.gcp_service_account_email
    },
    {
      name  = "k8s_service_account_name"
      value = local.us_publisher_k8s_service_account_name
    },
  ]
}

module "us_publisher_cluster" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/kubernetes"

  cluster_name           = "us-publisher-java"
  region                 = var.region
  zones                  = var.zones
  network_self_link      = google_compute_network.primary.self_link
  project_id             = data.google_project.project.project_id
  gcp_service_account_id = "us-publisher-java"
  gcp_service_account_iam_roles = [
    "roles/pubsub.publisher",
  ]
  k8s_namespace_name       = local.us_publisher_namespace
  k8s_service_account_name = local.us_publisher_k8s_service_account_name
  labels                   = var.labels
}

module "us_publisher_base_helm" {
  source = "./modules/helm"

  providers = {
    helm = helm.us_publisher_helm
  }
  chart_folder_name = "base"
  region            = var.region
  entries           = local.us_publisher_base_entries
}

module "us_publisher_helm" {
  depends_on = [
    module.us_publisher_base_helm,
  ]
  source = "./modules/helm"

  providers = {
    helm = helm.us_publisher_helm
  }
  chart_folder_name = "publisher"
  region            = var.region
  entries = concat(local.us_publisher_base_entries,
    [
      {
        name  = "project_id"
        value = data.google_project.project.project_id
      },
      {
        name  = "region"
        value = var.region
      },
      {
        name  = "image"
        value = var.publisher_image_url
      },
      {
        name  = "config_maps.event_topic"
        value = google_pubsub_topic.event.name
      },
    ]
  )
}
