module "project_factory" {
  source  = "terraform-google-modules/project-factory/google"
  version = "14.1.0"

  name                    = "ci-gke"
  random_project_id       = "true"
  org_id                  = var.org_id
  folder_id               = var.folder_id
  billing_account         = var.billing_account
  default_service_account = "keep"

  activate_apis = [
    "sourcerepo.googleapis.com",
    "cloudbuild.googleapis.com",
    "storage.googleapis.com",
    "secretmanager.googleapis.com",
    "iam.googleapis.com",
    "cloudresourcemanager.googleapis.com",
    "container.googleapis.com",
    "file.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
    "iam.googleapis.com"
  ]
}