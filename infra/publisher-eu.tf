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
  europe_north1_publisher_namespace                = "europe-north1-publisher"
  europe_north1_publisher_k8s_service_account_name = "europe-north1-publisher"
  europe_north1_base_entries = [
    {
      name  = "namespace"
      value = local.europe_north1_publisher_namespace
    },
    {
      name  = "gcp_service_account_email"
      value = module.europe_north1_publisher_cluster.gcp_service_account_email
    },
    {
      name  = "k8s_service_account_name"
      value = local.europe_north1_publisher_k8s_service_account_name
    },
  ]
}

module "europe_north1_publisher_cluster" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/kubernetes"

  cluster_name            = "europe-north1-publisher-java"
  region                  = "europe-north1"
  zones                   = ["europe-north1-a"]
  xwiki_network_self_link = google_compute_network.primary.self_link
  project_id              = data.google_project.project.project_id
  gcp_service_account_id  = "europe-north1-publisher-java"
  gcp_service_account_iam_roles = [
    "roles/pubsub.publisher",
  ]
  k8s_namespace_name       = local.europe_north1_publisher_namespace
  k8s_service_account_name = local.europe_north1_publisher_k8s_service_account_name
  labels                   = var.labels
}

module "europe_north1_publisher_base_helm" {
  source = "./modules/helm"

  providers = {
    helm = helm.europe_north1_publisher_helm
  }
  chart_folder_name = "base"
  region            = "europe-north1"
  entries           = local.europe_north1_base_entries
}

module "europe_north1_publisher_helm" {
  depends_on = [
    module.europe_north1_publisher_cluster,
  ]
  source = "./modules/helm"

  providers = {
    helm = helm.europe_north1_publisher_helm
  }
  chart_folder_name = "publisher"
  region            = "europe-north1"
  entries = concat(local.europe_north1_base_entries,
    [
      {
        name  = "project_id"
        value = data.google_project.project.project_id
      },
      {
        name  = "region"
        value = "europe-north1"
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
