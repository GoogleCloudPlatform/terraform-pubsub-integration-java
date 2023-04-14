locals {
  roles = [
    "roles/owner",
  ]
}

resource "google_service_account" "test" {
  project    = module.project_factory.project_id
  account_id = "ci-hsa"
}

resource "google_project_iam_member" "test" {
  for_each = toset(local.roles)

  project = module.project_factory.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.test.email}"
}

resource "google_service_account_key" "test" {
  service_account_id = google_service_account.test.id
}