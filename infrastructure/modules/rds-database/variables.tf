variable "database_parameter_group_name" {
  type        = string
  description = "Value for this DB engine version"
}

variable "database_engine" {
  type        = string
  description = "Engine value for this DB"
}

variable "database_engine_version" {
  type        = string
  description = "Value of DB engine version"
}

variable "database_availability_zone" {
  type    = string
  description ="Availability zone for database"
}

variable "database_engine_mode" {
  type        = string
  description = "Engine mode value for this DB"
}

variable "database_instance_class" {
  type        = string
  description = "Value for Aurora MySQL DB Instance Class"
}

variable "database_subnet_group_name" {
  type = string
  default = "default-vpc-8eb6abe8"
  description = "The subnet group name for the default VPC"
}

variable "database_username" {
  type        = string
  description = "Master username for this DB cluster"
}

variable "database_password" {
  type        = string
  description = "Master password for this DB cluster"
}

variable "database_name" {
  type        = string
  description = "Name of the initial database to be created on the cluster"
}

variable "database_port" {
  type        = number
  description = "Service port value for this DB instance"
}

variable "database_cluster_name" {
  type        = string
  description = "Name of the cluster for this DB"
}

variable "database_instance_name" {
  type        = string
  description = "Name of the writer instance for this DB"
}

variable "database_final_snapshot_name" {
  type        = string
  description = "Name of the final snapshot prior to deletion of the cluster"
}

variable "database_security_group_ids" {
  type        = list(string)
  description = "An list of security group IDs to associate with this database."
}

variable "database_minimum_capacity" {
  type = number
  default = 1
  description = "Minimum number of vCPUs to scale down to"
}

variable "database_maximum_capacity" {
  type = number
  description = "Maximum number of vCPUs to scale down to"
}