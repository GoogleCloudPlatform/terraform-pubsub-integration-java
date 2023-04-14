resource "google_container_cluster" "control_plane" {
  name             = var.cluster_name
  location         = var.region
  node_locations   = var.zones
  network          = var.xwiki_network_self_link
  networking_mode  = "VPC_NATIVE"
  enable_autopilot = true
  ip_allocation_policy {
  }
  resource_labels = var.labels
}

resource "google_service_account" "gcp" {
  account_id  = var.gcp_service_account_id
  description = "This sa is created by terraform and being used to bind k8s sa"
}

resource "google_project_iam_member" "gcp" {
  for_each = toset(var.gcp_service_account_iam_roles)

  project = var.project_id
  role    = each.key
  member  = "serviceAccount:${google_service_account.gcp.email}"
}

resource "google_service_account_iam_binding" "k8s" {
  for_each = toset([
    "roles/iam.workloadIdentityUser",
  ])
  service_account_id = google_service_account.gcp.name
  role               = each.value

  members = [
    "serviceAccount:${var.project_id}.svc.id.goog[${var.k8s_namespace_name}/${var.k8s_service_account_name}]"
  ]
}
