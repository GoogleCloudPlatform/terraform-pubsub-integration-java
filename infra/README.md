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
