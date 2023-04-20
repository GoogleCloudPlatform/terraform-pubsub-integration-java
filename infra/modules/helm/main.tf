locals {
  helm_release_name = replace(var.chart_folder_name, "/", "-")
  default_entries = [
    {
      name  = "operator"
      value = "terraform-helm-${substr(uuid(), 0, 4)}"
    },
  ]
}

resource "random_id" "code" {
  byte_length = 4
}

resource "helm_release" "manifest" {
  name  = "${local.helm_release_name}-${var.region}-${random_id.code.hex}"
  chart = "${path.module}/../../../config/helm/${var.chart_folder_name}"
  values = [
    file("${path.module}/../../../config/helm/${var.chart_folder_name}/values.yaml"),
  ]
  dynamic "set" {
    for_each = var.entries == null ? local.default_entries : concat(local.default_entries, var.entries)
    iterator = entry
    content {
      name  = entry.value.name
      value = entry.value.value
    }
  }
  dynamic "set_sensitive" {
    for_each = var.secret_entries == null ? [] : var.secret_entries
    iterator = secret_entry
    content {
      name  = secret_entry.value.name
      value = secret_entry.value.value
    }
  }
}
