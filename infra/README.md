# Infrastructure Usage

Before you spin up infrastructure, you must set some config parameters as following example:

- Create  **terraform.tfvars** file.
- **terraform.tfvars** file's content :

```
project_id           = "<your gcp project id>"
```

if you want to use your own image, you can add image-url as following :

```
project_id           = "<your gcp project id>"
publisher_image_url  = "<your publisher_image_url>"
subscriber_image_url = "<your subscriber_image_url>"
```

---

To spin up the infrastructure, run in this folder:

```shell
terraform init
terraform apply
```

To teardown the infrastructure, run:

```shell
terraform destroy
```

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| labels | A map of key/value label pairs to assign to the resources. | `map(string)` | <pre>{<br>  "app": "gcp-api-integration-java"<br>}</pre> | no |
| project\_id | GCP project ID. | `string` | n/a | yes |
| publisher\_image\_url | pubsub publisher app image url | `string` | `"asia.gcr.io/aemon-projects-dev-012/pubsub-pub:0503"` | no |
| region | google cloud region where the resource will be created. | `string` | `"us-west1"` | no |
| subscriber\_image\_url | pubsub subscriber app image url | `string` | `"asia.gcr.io/aemon-projects-dev-012/pubsub-sub:0503"` | no |

## Outputs

| Name | Description |
|------|-------------|
| errors\_topic\_name | The name of the error topic |
| event\_topic\_name | The name of the event topic |
| metrics\_topic\_name | The name of the metric topic |
| project\_id | GCP project ID. |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
