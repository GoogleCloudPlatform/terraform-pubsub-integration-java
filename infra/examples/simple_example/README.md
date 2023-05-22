# Simple Example

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| project\_id | GCP project for provisioning cloud resources. | `any` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| bq\_table\_id | The ID of the BigQuery table |
| errors\_topic\_name | The name of the error topic |
| europe\_north1\_publisher\_cluster\_name | The name of the GKE cluster in the europe-north1 region used by the publisher. |
| europe\_north1\_publisher\_cluster\_namespace | The namespace of the GKE cluster in the europe-north1 region used by the publisher. |
| event\_subscription\_name | The name of the event subscription created for Pub/Sub. |
| event\_topic\_name | The name of the event topic |
| metrics\_schema\_name | The name of the metrics schema |
| metrics\_subscription\_name | The name of the metrics subscription created for Pub/Sub. |
| metrics\_topic\_name | The name of the metric topic |
| us\_west1\_publisher\_cluster\_name | The name of the GKE cluster in the us-west1 region used by the publisher. |
| us\_west1\_publisher\_cluster\_namespace | The namespace of the GKE cluster in the us-west1 region used by the publisher. |
| us\_west1\_subscriber\_cluster\_name | The name of the GKE cluster in the us-west1 region used by the publisher. |
| us\_west1\_subscriber\_cluster\_namespace | The namespace of the GKE cluster in the us-west1 region used by the subscriber. |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
