# Infrastructure for Cloud Pub/Sub Integration

Jump start solution infrastructure

### Tagline
Sample JSS infrastructure tagline.

### Detailed
Prerequisite

- Create  **terraform.tfvars** file.
- **terraform.tfvars** file's content :

```
project_id           = "<your gcp project id>"
```

- Use the following commands to deploy the infrastructure in the current folder:

```shell
terraform init
terraform apply
```

- To teardown the infrastructure, use the following command:

```shell
terraform destroy
```

### Architecture
- Google Kubernetes Engine (GKE)
- Cloud Pub/Sub
- BigQuery


## Documentation
- [Architecture Diagram](todo)

---

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| labels | A map of key/value label pairs to assign to the resources. | `map(string)` | <pre>{<br>  "app": "gcp-api-integration-java"<br>}</pre> | no |
| project\_id | GCP project ID. | `string` | n/a | yes |
| publisher\_image\_url | pubsub publisher app image url | `string` | `"gcr.io/aemon-projects-dev-000/jss-psi-java-event-generator:latest"` | no |
| publisher\_region | publisher region where the resource will be created. | `string` | `"europe-north1"` | no |
| publisher\_zones | publisher zones where the resource will be created. | `list(string)` | <pre>[<br>  "europe-north1-a"<br>]</pre> | no |
| region | google cloud region where the resource will be created. | `string` | `"us-west1"` | no |
| subscriber\_image\_url | pubsub subscriber app image url | `string` | `"gcr.io/aemon-projects-dev-000/jss-psi-java-metrics-ack:latest"` | no |
| zones | google cloud zones where the resource will be created. | `list(string)` | <pre>[<br>  "us-west1-a"<br>]</pre> | no |

## Outputs

| Name | Description |
|------|-------------|
| bq\_table\_id | The ID of the BigQuery table |
| errors\_topic\_name | The name of the error topic |
| eu\_publisher\_cluster\_info | The cluster information for the publisher cluster in eu |
| event\_subscription\_name | The name of the event subscription created for Cloud Pub/Sub |
| event\_topic\_name | The name of the event topic |
| metrics\_schema\_name | The name of the metrics schema |
| metrics\_subscription\_name | The name of the metrics subscription created for Cloud Pub/Sub |
| metrics\_topic\_name | The name of the metric topic |
| project\_id | The ID of the project where resources are deployed to |
| us\_publisher\_cluster\_info | The cluster information for the publisher cluster in us |
| us\_subscriber\_cluster\_info | The cluster information for the subscriber cluster in us |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
