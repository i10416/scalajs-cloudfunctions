
# Example repository for ScalaJS on GCP Cloud Functions with Github Actions CI/CD

There is also [giter8 template repository](https://github.com/i10416/scalajs-cloudfunctions.g8).

If you prefer giter8, run `g8 i10416/scalajs-cloudfunctions.g8`.

## How To Use

Replace `$***`s with your own setting.

Main.scala

```scala

package $packageName
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.{Function2 => JSFunction2}

```

function.tf

```
  backend "gcs" {
    # edit these fields
    bucket = "$tfstateBucketName"
    prefix = "tfstate/$functionName"
  }
}
```

## prerequisites
- node
- sbt
- gcloud command
- gsutil command

## Debug

```sh
sbt fullOptJS
```


```sh
npm install -g @google-cloud/functions-framework
```

```sh
functions-framework --target=$functionName
```

## Deploy


### Setup GCP

```sh
gcloud services enable iamcredentials.googleapis.com --project "$GCPProjectID"
```

Create Service Account for Terraform

```sh
gcloud iam service-accounts create terraform
```

```sh
// bind iam
```

Configure Workload Identity Pool and Provider


```sh
export POOL_NAME=$POOL_NAME
```

```sh
cloud iam workload-identity-pools create "$POOL_NAME" \
    --project=$GCPProjectID --location="global" \
    --display-name="use from GitHub Actions"
```

```sh
export WORKLOAD_IDENTITY_POOL_ID=$( \
    gcloud iam workload-identity-pools describe "$POOL_NAME" \
      --project="$GCPProjectID" --location="global" \
      --format="value(name)" \
  )
```

```sh
export TF_SERVICE_AGENT=
```

```sh
gcloud iam service-accounts add-iam-policy-binding "${TF_SERVICE_AGENT}" \
    --project="$GCPProjectID" \
    --role="roles/iam.workloadIdentityUser" \
    --member="principalSet://iam.googleapis.com/${WORKLOAD_IDENTITY_POOL_ID}/attribute.repository/${GH_USER}/${GH_REPO}"
```


### Environment Variables


#### Github Actions

| name                        | example value                                                                              | description                |
| --------------------------- | ------------------------------------------------------------------------------------------ | -------------------------- |
| TF_CLI_VERSION              | 1.1.19                                                                                     | terraform version          |
| TF_GCP_SERVICE_ACCOUNT      | terraform@project.iam.gserviceaccount.com                                                  | terraform service account  |
| GCP_PROJECT_ID              |                                                                                            | gcp project id             |
| GCP_REGION                  | asia-northeast1                                                                            | gcp region                 |
| GCP_FUNCTION_NAME           | main                                                                                       | cloud function entrypoint  |
| GCP_FUNCTION_BUCKET         | main-bucket                                                                                | cloud function bucket name |
| GCP_WORKLOAD_ID_PROVIDER_ID | projects/project_number/locations/global/workloadIdentityPolls/pool_id/providers/oidc-name |                            |

