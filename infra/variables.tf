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
  default     = "asia.gcr.io/aemon-projects-dev-012/pubsub-sub:0502"
}

variable "publisher_image_url" {
  description = "pubsub publisher app image url "
  type        = string
  default     = "asia.gcr.io/aemon-projects-dev-012/pubsub-pub:0502"
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
  default = {
    app = "gcp-api-integration"
  }
}
