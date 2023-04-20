locals {
  us_west1_subscriber_namespace                = "us-west1-subscriber"
  us_west1_subscriber_k8s_service_account_name = "us-west1-subscriber"
  us_west1_subscriber_base_entries = [
    {
      name  = "namespace"
      value = local.us_west1_subscriber_namespace
    },
    {
      name  = "gcp_service_account_email"
      value = module.us_west1_subscriber_cluster.gcp_service_account_email
    },
    {
      name  = "k8s_service_account_name"
      value = local.us_west1_subscriber_k8s_service_account_name
    },
  ]
}

module "us_west1_subscriber_cluster" {
  depends_on = [
    module.project_services,
  ]
  source = "./modules/kubernetes"

  cluster_name            = "us-west1-subscriber"
  region                  = "us-west1"
  zones                   = ["us-west1-a"]
  xwiki_network_self_link = google_compute_network.primary.self_link
  project_id              = data.google_project.current.project_id
  gcp_service_account_id  = "us-west1-subscriber"
  gcp_service_account_iam_roles = [
    "roles/pubsub.subscriber",
    "roles/pubsub.publisher",
  ]
  k8s_namespace_name       = local.us_west1_subscriber_namespace
  k8s_service_account_name = local.us_west1_subscriber_k8s_service_account_name
  labels                   = var.labels
}

provider "helm" {
  alias = "us_west1_subscriber_helm"
  kubernetes {
    host                   = "https://${module.us_west1_subscriber_cluster.control_plane.endpoint}"
    token                  = data.google_client_config.default.access_token
    cluster_ca_certificate = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].cluster_ca_certificate, )
    client_certificate     = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].client_certificate)
    client_key             = base64decode(module.us_west1_subscriber_cluster.control_plane.master_auth[0].client_key)
  }
}

module "us_west1_subscriber_base_helm" {
  depends_on = [
    module.us_west1_subscriber_cluster,
  ]
  source = "./modules/helm"

  providers = {
    helm = helm.us_west1_subscriber_helm
  }
  chart_folder_name = "base"
  region            = "us-west1"
  entries           = local.us_west1_subscriber_base_entries
}

module "us_west1_subscriber_helm" {
  depends_on = [
    module.us_west1_subscriber_base_helm,
  ]
  source = "./modules/helm"

  providers = {
    helm = helm.us_west1_subscriber_helm
  }
  chart_folder_name = "subscriber"
  region            = "us-west1"
  entries = concat(local.us_west1_subscriber_base_entries,
    [
      {
        name  = "project_id"
        value = data.google_project.current.project_id
      },
      {
        name  = "region"
        value = "us-west1"
      },
      {
        name  = "image"
        value = var.subscriber_image_url
      },
      {
        name  = "config_maps.event_subscription"
        value = google_pubsub_subscription.events.id
      },
      {
        name  = "config_maps.metric_topic"
        value = google_pubsub_topic.metrics.id
      },
      {
        name  = "config_maps.metric_app_port"
        value = "8001"
      },
    ]
  )
}
