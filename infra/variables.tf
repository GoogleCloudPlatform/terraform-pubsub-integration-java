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

variable "project_id" {
  description = "GCP project ID."
  type        = string
  validation {
    condition     = var.project_id != ""
    error_message = "Error: project_id is required"
  }
}

variable "region" {
  description = "google cloud region where the resource will be created."
  type        = string
  default     = "us-west1"
}

variable "zones" {
  description = "google cloud zones where the resource will be created."
  type        = list(string)
  default     = ["us-west1-a"]
}

variable "publisher_region" {
  description = "publisher region where the resource will be created."
  type        = string
  default     = "europe-north1"
}

variable "publisher_zones" {
  description = "publisher zones where the resource will be created."
  type        = list(string)
  default     = ["europe-north1-a"]
}

variable "subscriber_image_url" {
  description = "pubsub subscriber app image url "
  type        = string
  default     = "gcr.io/aemon-projects-dev-000/jss-psi-java-metrics-ack:latest"
}

variable "publisher_image_url" {
  description = "pubsub publisher app image url "
  type        = string
  default     = "gcr.io/aemon-projects-dev-000/jss-psi-java-event-generator:latest"
}

variable "labels" {
  description = "A map of key/value label pairs to assign to the resources."
  type        = map(string)
  default = {
    app = "gcp-api-integration-java"
  }
}
