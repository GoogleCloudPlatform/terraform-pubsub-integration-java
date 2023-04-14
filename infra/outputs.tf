output "project_id" {
  description = "GCP project ID."
  value       = data.google_project.current.project_id
}
