output "dataset_id" {
  description = "The id of the dataset"
  value       = google_bigquery_dataset.main.dataset_id
}

output "table_id" {
  description = "The id of the table"
  value       = google_bigquery_table.main.table_id
}
