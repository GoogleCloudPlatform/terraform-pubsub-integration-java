variable "chart_folder_name" {
  description = "The signified folder's name in chars folder"
  type        = string
}

variable "region" {
  description = "The region to host the k8s cluster"
  type        = string
}

variable "entries" {
  description = "custom values to be merge into values yaml."
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}

variable "secret_entries" {
  description = "custom sensitive values to be merged into values yaml. it would not be exposed in the terraform plan's diff."
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}

variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
  default     = {}
}
