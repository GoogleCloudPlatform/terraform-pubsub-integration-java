/**
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

resource "google_bigquery_dataset" "main" {
  dataset_id = var.dataset_id
  labels     = var.labels
}

resource "google_bigquery_table" "main" {
  deletion_protection = false
  table_id            = var.table_id
  dataset_id          = google_bigquery_dataset.main.dataset_id
  schema              = var.schema
  labels              = var.labels
}
