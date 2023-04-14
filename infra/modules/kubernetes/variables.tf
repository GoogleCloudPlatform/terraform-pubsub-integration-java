variable "cluster_name" {
  description = "The name of the cluster"
  type        = string
}

variable "region" {
  description = "The region to host the k8s cluster"
  type        = string
}

variable "zones" {
  description = "The zones to host the k8s cluster"
  type        = list(string)
}

variable "xwiki_network_self_link" {
  description = "The VPC network self_link to host the k8s cluster"
  type        = string
}

variable "project_id" {
  description = "GCP project ID."
  type        = string
  validation {
    condition     = var.project_id != ""
    error_message = "Error: project_id is required"
  }
}

variable "gcp_service_account_id" {
  description = "gcp service account's id"
  type        = string
}

variable "gcp_service_account_iam_roles" {
  description = "the list of permissions for gcp service account"
  type        = list(string)
}

variable "k8s_namespace_name" {
  description = "kubernetes namespace name"
  type        = string
}

variable "k8s_service_account_name" {
  description = "kubernetes service account for pods"
  type        = string
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
  default     = {}
}
