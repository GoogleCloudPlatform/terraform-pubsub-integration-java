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
  eu_publisher_namespace                = "${var.publisher_region}-publisher"
  eu_publisher_k8s_service_account_name = "${var.publisher_region}-publisher"
  eu_base_entries = [
    {
      name  = "namespace"
      value = local.eu_publisher_namespace
    },
    {
      name  = "gcp_service_account_email"
      value = module.eu_publisher_cluster.gcp_service_account_email
    },
    {
      name  = "k8s_service_account_name"
      value = local.eu_publisher_k8s_service_account_name
    },
  ]
}

module "eu_publisher_cluster" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/kubernetes"

  cluster_name           = "eu-publisher-java"
  region                 = var.publisher_region
  zones                  = var.publisher_zones
  network_self_link      = google_compute_network.primary.self_link
  project_id             = data.google_project.project.project_id
  gcp_service_account_id = "eu-publisher-java"
  gcp_service_account_iam_roles = [
    "roles/pubsub.publisher",
  ]
  k8s_namespace_name       = local.eu_publisher_namespace
  k8s_service_account_name = local.eu_publisher_k8s_service_account_name
  labels                   = var.labels
}

module "eu_publisher_base_helm" {
  source = "./modules/helm"

  providers = {
    helm = helm.eu_publisher_helm
  }
  chart_folder_name = "base"
  region            = var.publisher_region
  entries           = local.eu_base_entries
}

module "eu_publisher_helm" {
  depends_on = [
    module.eu_publisher_base_helm,
  ]
  source = "./modules/helm"

  providers = {
    helm = helm.eu_publisher_helm
  }
  chart_folder_name = "publisher"
  region            = var.publisher_region
  entries = concat(local.eu_base_entries,
    [
      {
        name  = "project_id"
        value = data.google_project.project.project_id
      },
      {
        name  = "region"
        value = var.publisher_region
      },
      {
        name  = "image"
        value = var.publisher_image_url
      },
      {
        name  = "config_maps.event_topic"
        value = google_pubsub_topic.event.id
      },
    ]
  )
}
