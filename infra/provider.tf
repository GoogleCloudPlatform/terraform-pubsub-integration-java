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

terraform {
  required_version = ">= 0.13"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.53"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.4"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.9"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

provider "helm" {
  alias = "europe_north1_publisher_helm"
  kubernetes {
    host                   = "https://${module.europe_north1_publisher_cluster.control_plane.endpoint}"
    token                  = data.google_client_config.default.access_token
    cluster_ca_certificate = base64decode(module.europe_north1_publisher_cluster.control_plane.master_auth[0].cluster_ca_certificate)
    client_certificate     = base64decode(module.europe_north1_publisher_cluster.control_plane.master_auth[0].client_certificate)
    client_key             = base64decode(module.europe_north1_publisher_cluster.control_plane.master_auth[0].client_key)
  }
}

provider "helm" {
  alias = "us_west1_publisher_helm"
  kubernetes {
    host                   = "https://${module.us_west1_publisher_cluster.control_plane.endpoint}"
    token                  = data.google_client_config.default.access_token
    cluster_ca_certificate = base64decode(module.us_west1_publisher_cluster.control_plane.master_auth[0].cluster_ca_certificate)
    client_certificate     = base64decode(module.us_west1_publisher_cluster.control_plane.master_auth[0].client_certificate)
    client_key             = base64decode(module.us_west1_publisher_cluster.control_plane.master_auth[0].client_key)
  }
}

provider "helm" {
  alias = "us_west1_subscriber_helm"
  kubernetes {
    host                   = "https://${module.us_west1_subscriber_cluster.control_plane.endpoint}"
    token                  = data.google_client_config.default.access_token
    cluster_ca_certificate = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].cluster_ca_certificate)
    client_certificate     = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].client_certificate)
    client_key             = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].client_key)
  }
}
