resource "google_bigquery_dataset" "main" {
  dataset_id = var.dataset_id
  labels     = var.labels
}

resource "google_bigquery_table" "main" {
  deletion_protection = false
  table_id            = var.table_id
  dataset_id          = google_bigquery_dataset.main.dataset_id
  schema              = var.schema
  labels              = var.labels
}
