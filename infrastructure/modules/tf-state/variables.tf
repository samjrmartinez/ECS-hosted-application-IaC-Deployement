variable "save_state_bucket_name" {
  type = string
  description = "S3 bucket name to saving terraform state"
  validation {
    condition = can(regex("^([a-z0-9]{1}[a-z0-9-]{1,61}[a-z0-9]{1})$", var.save_state_bucket_name))
    error_message = "Bucket Name must not be empty and must follow S3 naming rules"
  }
}

variable "dynamo_lock_table_name" {
  type = string
  description = "Dynamo DB Table name to manage locks"
}