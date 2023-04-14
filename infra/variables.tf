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

variable "subscriber_image_url" {
  description = "pubsub subscriber app image url "
  type        = string
  //complete
  default = "asia.gcr.io/aemon-projects-dev-012/pubsub-sub@sha256:3cf2bc1a61787d9c4f30989e4a6d5cef8e5a063b1f6e84bb06cf7bfc841e6fb5"
}

variable "publisher_image_url" {
  description = "pubsub publisher app image url "
  type        = string
  default     = "asia.gcr.io/aemon-projects-dev-012/pubsub-pub@sha256:54d999a9e2d492dcf43a9149591df4ff1d80211a18865513e265fdafedbfb5f3"
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
  default = {
    app = "gcp-api-integration"
  }
}
