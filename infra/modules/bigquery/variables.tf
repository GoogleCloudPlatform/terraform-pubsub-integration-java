variable "dataset_id" {
  description = "The id of the dataset"
  type        = string
}

variable "table_id" {
  description = "The id of the table"
  type        = string
}

variable "schema" {
  description = "table json schema"
  type        = string
}


variable "labels" {
  type        = map(string)
  description = "A map of key/value label pairs to assign to the resources."
  default     = {}
}
