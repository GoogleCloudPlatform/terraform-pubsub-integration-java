output "control_plane" {
  description = "Cluster informations"
  value       = google_container_cluster.control_plane
}

output "gcp_service_account_email" {
  description = "gcp service account's email"
  value       = google_service_account.gcp.email
}
