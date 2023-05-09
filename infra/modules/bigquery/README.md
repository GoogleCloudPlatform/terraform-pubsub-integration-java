# BIGQUERY

<!-- BEGINNING OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| dataset\_id | The id of the dataset | `string` | n/a | yes |
| labels | A map of key/value label pairs to assign to the resources. | `map(string)` | `{}` | no |
| schema | table json schema | `string` | n/a | yes |
| table\_id | The id of the table | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| dataset\_id | The id of the dataset |
| table\_id | The id of the table |

<!-- END OF PRE-COMMIT-TERRAFORM DOCS HOOK -->
