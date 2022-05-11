terraform {
  required_version = "~> 1.1.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.17.0"
    }
  }

  backend "gcs" {
    # edit these fields
    bucket = "$tfstateBucketName"
    prefix = "tfstate/$functionName"
  }
}

provider "google" {
  project = var.project_id
}

resource "google_storage_bucket" "bucket" {
  name     = var.function_bucket
  location = var.region
}

data "archive_file" "function_src" {
  type        = "zip"
  source_dir  = var.dist_dir
  output_path = "tmp/function.zip"
  excludes    = ["index.js.map"]
}

resource "google_storage_bucket_object" "function_src" {
  name   = "functions/${var.functionName}-${data.archive_file.function_src.output_md5}.zip"
  bucket = google_storage_bucket.bucket.name
  source = data.archive_file.function_src.output_path
}

resource "google_cloudfunctions_function" "function" {
  name                  = var.function_name
  description           = "$description"
  runtime               = "nodejs14"
  region                = var.region
  available_memory_mb   = 128
  timeout               = 30
  source_archive_bucket = google_storage_bucket.bucket.name
  source_archive_object = google_storage_bucket_object.function_src.name
  trigger_http          = true
  entry_point           = var.function_name

  labels = {
  }

  environment_variables = {

  }
}


resource "google_cloudfunctions_function_iam_member" "function_invoker" {
  project        = google_cloudfunctions_function.functio.project
  region         = google_cloudfunctions_function.function.region
  cloud_function = google_cloudfunctions_function.function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}
